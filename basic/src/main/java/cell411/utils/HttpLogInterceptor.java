package cell411.utils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpLogInterceptor implements Interceptor {
  public static final String TAG = Reflect.getTag();
  int mReqNum = 0;

  @NonNull @Override public Response intercept(@NonNull Chain chain) throws IOException {
    Request req = chain.request();
    String msg = "Req: " + (++mReqNum) + ":  method=" + req.method() + " url=" + req.url();
    XLog.i(TAG, msg);
    Response res = chain.proceed(req);
    if (!res.isSuccessful()) {
      XLog.i(TAG, "     " + mReqNum + ":  full_res=" + res);
      XLog.i(TAG, "     " + mReqNum + ":  full_req=" + req);
    }
    XLog.i(TAG, "Res: " + mReqNum + ":  code=" + res.code() + " message: " + res.message());
    return res;
  }
}
