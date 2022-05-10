package com.parse.http;

import android.util.Log;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.codec.ParseDecoder;
import com.parse.codec.PointerEncoder;
import com.parse.controller.ParseCloudCodeController;
import com.parse.controller.ParseFileController;
import com.parse.controller.ParsePlugins;
import com.parse.model.ParseFile;
import com.parse.model.ParseObject;
import com.parse.model.ParseUser;
import com.parse.rest.ParseHttpClient;
import com.parse.rest.ParseRESTCloudCommand;
import com.parse.rest.ParseRESTCommand;
import com.parse.rest.ParseRESTFileCommand;
import com.parse.rest.ParseRESTQueryCommand;
import com.parse.rest.ParseRequest;
import com.parse.utils.ParseFileUtils;
import com.parse.utils.ParseIOUtils;
import cell411.json.JSONArray;
import cell411.json.JSONException;
import cell411.json.JSONObject;
import cell411.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.parse.ParseException.OTHER_CAUSE;

public class ParseSyncUtils {
  public static JSONObject getJSON(ParseHttpResponse response) {
    InputStream responseStream = null;
    try {
      responseStream = response.getContent();
      String content = new String(ParseIOUtils.toByteArray(responseStream)).trim();
      if (content.substring(0, 6).equalsIgnoreCase("<html>")) {
        if (content.contains("<title>502 Bad Gateway")) {
          throw new ParseException(ParseException.CONNECTION_FAILED, "502 Bad Gateway");
        } else {
          throw new ParseException(ParseException.OTHER_CAUSE, content);
        }
      } else {
        return new JSONObject(content);
      }
    } catch (IOException e) {
      throw new RuntimeException("Reading query response", e);
    } catch (JSONException e) {
      throw new RuntimeException("Parsing query response", e);
    } finally {
      ParseIOUtils.closeQuietly(responseStream);
    }
  }

  public static JSONObject executeRequest(ParseHttpClient client, ParseHttpRequest request)
    throws IOException
  {
    ParseHttpResponse response     = client.execute(request);
    JSONObject        jsonResponse = getJSON(response);
    int               statusCode   = response.getStatusCode();
    switch (statusCode / 100) {
      case 4:
      case 5: {
        int    code    = jsonResponse.optInt("code");
        String message = jsonResponse.optString("error");
        // Internal error server side, or something, we'll
        // flag that it might be worth trying again.
        ParseRequest.ParseRequestException e =
          new ParseRequest.ParseRequestException(code, message);
        e.mIsPermanentFailure = statusCode < 500;
        throw e;
      }
      case 2:
        return jsonResponse;
      default:
        throw new ParseException(ParseException.OTHER_CAUSE, "Unexpected http code");
    }
  }
//  public static JSONObject fileToJSON(File cacheFile) {
//    String string  = IOUtil.fileToString(cacheFile);
//    Object decoded = ParseSyncUtils.decode(string);
//    if (decoded instanceof Map) {
//      convertMap((Map<?, ?>) decoded, data);
//    } else if (decoded instanceof JSONObject) {
//      decodeJSON((JSONObject) decoded, data);
//    } else {
//      showAlertDialog("Expected Map or JSONObject, got " + decoded);
//    }
//  }

  public static void decodeJSON(JSONObject decoded, HashMap<String, Object> data) {
    try {
      for (Iterator<String> it = decoded.keys(); it.hasNext(); ) {
        String key = it.next();
        Object val = decoded.get(key);
        data.put(key, val);
      }
    } catch (JSONException e) {
      throw new RuntimeException("Decoding JSON", e);
    }
  }
  public static void convertMap(Map<?, ?> from, HashMap<String, Object> to) {
    for (Object key : from.keySet()) {
      Object val = from.get(key);
      assert key instanceof String;
      to.put((String) key, val);
    }
  }


  public static <T extends ParseObject> List<T> find(ParseQuery.State<T> state)
    throws ParseException
  {
    ParseException pe = null;
    Throwable      th = null;
    try {
      JSONObject jsonResponse    = runQuery(state, false);
      JSONArray  jsonResults     = jsonResponse.getJSONArray("results");
      String     resultClassName = jsonResponse.optString("className");
      if (resultClassName.isEmpty())
        resultClassName = state.className();
      List<T> results = new ArrayList<>(jsonResults.length());
      for (int i = 0; i < jsonResults.length(); i++) {
        JSONObject jsonResult = jsonResults.getJSONObject(i);
        T result = ParseObject.fromJSON(jsonResult, resultClassName, ParseDecoder.get(),
                                        state.selectedKeys());
        results.add(result);
      }
      return results;
    } catch (ParseException e) {
      th = pe = e;
    } catch (Throwable t) {
      th = t;
    }
    try {
      ByteArrayOutputStream text       = new ByteArrayOutputStream();
      PrintStream           printer    = new PrintStream(text);
      JSONObject            jsonObject = state.toJSON(PointerEncoder.get());
      String                strQuery   = "\\n" + jsonObject.toString(2) + "\\n\\n";
      strQuery = strQuery.replaceAll("\\n", "\\n  |");
      printer.println("While running the following query:\n");
      printer.println(strQuery);
      printer.println("\nWe encountered the following error:\n");
      if (pe != null)
        pe.printStackTrace(printer);
      else
        th.printStackTrace(printer);

      printer.println("\n\nDo something about that, would you?\n");
      printer.flush();
      Log.i("ParseSyncUtils", text.toString());
      if (pe != null)
        throw pe;
      else
        throw th;
    } catch (Throwable ignored) {
      if (pe != null)
        throw pe;
      else
        throw new RuntimeException("Rethrow", th);
    }
  }
  public static <T extends ParseObject> JSONObject runQuery(ParseQuery.State<T> state,
                                                            boolean count)
  {

    ParseHttpClient client = ParsePlugins.get().restClient();
    try {
      ParseUser currentUser = ParseUser.getCurrentUser();
      if (currentUser == null)
        throw new ParseException(ParseException.NOT_LOGGED_IN, "session missing");

      String              sessionToken = currentUser.getSessionToken();
      String              httpPath     = String.format("classes/%s", state.className());
      Map<String, String> parameters   = ParseRESTQueryCommand.encode(state, count);
      ParseRESTQueryCommand command =
        new ParseRESTQueryCommand(httpPath, ParseHttpMethod.GET, parameters, sessionToken);
      ParseHttpMethod  method  = command.method;
      String           url     = command.url;
      ParseHttpRequest request = command.newRequest(method, url, null);
      return executeRequest(client, request);
    } catch (ParseException pe) {
      throw new ParseException(pe.getCode(), "Running query for class " + state.className(), pe);
    } catch (IOException e) {
      throw new ParseException(OTHER_CAUSE, "Executing Query for class " + state.className(), e);
    }
  }

  public static <T extends ParseObject> int count(ParseQuery.State<T> state) {
    JSONObject result = runQuery(state, true);
    return result.optInt("count", 0);
  }
  public static Object decodeObject(Object result) {
    ParseDecoder decoder = ParseDecoder.get();
    return decoder.decode(result);
  }
  public static <T> T run(String name, Map<String, ?> params) {
    String                   sessionToken = ParseUser.getCurrentSessionToken();
    ParseCloudCodeController controller   = ParseCloud.getCloudCodeController();
    ParseRESTCommand command =
      ParseRESTCloudCommand.callFunctionCommand(name, params, sessionToken);
    try {
      ParseHttpClient  client       = controller.getRestClient();
      ParseHttpRequest request      = command.newRequest();
      JSONObject       jsonResponse = executeRequest(client, request);
      Object           result       = jsonResponse.opt("result");
      //noinspection unchecked
      return (T) ParseSyncUtils.decodeObject(result);
    } catch (IOException ioe) {
      throw command.newTemporaryException("Calling Function " + name, ioe);
    }
  }
  public static Object decode(byte[] fileToBytes) {
    return decode(new String(fileToBytes));
  }
  public static Object decode(String s) {
    JSONTokener tokener = new JSONTokener(s);
    try {
      return decodeObject(tokener.nextValue());
    } catch (JSONException je) {
      throw new RuntimeException("parsing json", je);
    }
  }
  public static <X extends ParseObject> JSONObject encodeQuery(ParseQuery<X> query) {
    PointerEncoder encoder = PointerEncoder.get();
    return query.getBuilder().build().toJSON(encoder);
  }
  public static void save(ParseFile parseFile) {
    if (!parseFile.isDirty())
      return;
    try {
      ParseUser       currentUser  = ParseUser.getCurrentUser();
      ParseFile.State state        = parseFile.state;
      byte[]          data         = parseFile.data;
      String          contentType  = state.mimeType();
      String          sessionToken = currentUser.getSessionToken();

      ParseRESTFileCommand.Builder builder = new ParseRESTFileCommand.Builder();
      builder.fileName(state.name());
      builder.data(data);
      builder.contentType(state.mimeType());
      builder.sessionToken(sessionToken);
      ParseRESTFileCommand command = builder.build();
      ParseHttpClient      client  = ParsePlugins.get().restClient();

      JSONObject              result       = executeRequest(client, command.newRequest());
      ParseFileController     controller   = parseFile.getFileController();
      ParseFile.State.Builder stateBuilder = new ParseFile.State.Builder();
      stateBuilder.name(result.getString("name"));
      stateBuilder.url(result.getString("url"));
      ParseFile.State newState  = stateBuilder.build();
      File            cacheFile = controller.getCacheFile(newState);
      try {
        ParseFileUtils.writeByteArrayToFile(cacheFile, data);
      } catch (IOException e) {
        // do nothing
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <X extends ParseObject> List<X> readList(String fileToString, Class<X> type) {
    Object  decoded = decode(fileToString);
    List<X> result  = new ArrayList<>();
    assert decoded instanceof List;
    List<?> list = (List<?>) decoded;
    for (Object item : list) {
      result.add(type.cast(item));
    }
    return result;
  }
}