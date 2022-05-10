package cell411.utils;

import cell411.json.JSONObject;
import com.parse.http.ParseSyncUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

@SuppressWarnings("unused")
public class IOUtil {
  private static final String   TAG       = "IOUtil";
  static               Catching mCatching = new Catching();
  public static byte[] streamToBytes(InputStream is) {
    return mCatching.streamToBytes(null, is);
  }
  public static String fileToString(File file)
  {
    InputStream is = mCatching.newInputStream(file);
    byte[]      bytes = mCatching.streamToBytes(file, is);
    return new String(bytes);
  }
  public static int stringToFile(File file, String save) {
    OutputStream stream = mCatching.newOutputStream(file);
    byte[]       bytes  = save.getBytes();
    return mCatching.bytesToStream(file, stream, bytes);
  }
  public static int bytesToFile(File file, byte[] bytes) {
    createNewFile(file);
    OutputStream stream = mCatching.newOutputStream(file);
    return mCatching.bytesToStream(file, stream, bytes);
  }
  public static byte[] fileToBytes(File file) {
    return mCatching.streamToBytes(file, mCatching.newInputStream(file));
  }
  /**
   * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
   * null.
   */
  public static void closeQuietly(Closeable closeable) {
    mCatching.closeQuietly(closeable);
  }
  static int count = 0;
  public static void delete(File cacheFile) {
    if (cacheFile.exists() && cacheFile.delete()) {
      ++count;
    } else {
      --count;
    }
    if (count == 0)
      XLog.i(TAG, "ZERO!");
  }

  public static void closeStream(OutputStream out) {
    if (out == null)
      return;
    try {
      out.close();
    } catch (Exception ignored) {

    }
  }
  public static void createNewFile(File photoFile) {
    try {
      if (!photoFile.createNewFile())
        throw new IOException("Cannot create " + photoFile + " file exists");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public static void mkdirs(File parentFile, boolean includeLast) {
    if (parentFile == null) {
      return;
    }
    if (includeLast) {
      if (parentFile.isDirectory()) {
        return;
      } else if (parentFile.exists()) {
        throw new RuntimeException(parentFile + " exists, and is not a directory");
      } else {
        mkdirs(parentFile.getParentFile(), true);
      }
      if (parentFile.mkdir()) {
        XLog.i(Util.TAG, "created: " + parentFile);
      }
    } else {
      mkdirs(parentFile.getParentFile(), true);
    }
  }
  public static void rename(File tempFile, File cacheFile) {
    tempFile.renameTo(cacheFile);
  }

  public static class Catching implements ExceptionForwarder {
    Catching() {
    }

    public byte[] streamToBytes(File file, InputStream is) {
      try {
        ByteArrayOutputStream os;
        try {
          os = new ByteArrayOutputStream(1024);
          byte[] buffer = new byte[8192];
          int    len;
          while ((len = is.read(buffer)) >= 0) {
            os.write(buffer, 0, len);
          }
          return os.toByteArray();
        } finally {
          closeQuietly(is);
        }
      } catch (Exception ex) {
        throw rethrowException(action("reading", file, is), ex, null);
      }
    }
    public InputStream newInputStream(File file) {
      try {
        return Files.newInputStream(file.toPath());
      } catch (Exception exception) {
        throw Util.rethrow(action("opening", file, null), exception);
      }
    }
    private OutputStream newOutputStream(File file) {
      try {
        return Files.newOutputStream(file.toPath());
      } catch (Exception exception) {
        throw Util.rethrow(action("opening", file, null), exception);
      }
    }

    public int bytesToStream(File file, OutputStream stream, byte[] bytes) {
      try {
        stream.write(bytes);
        return bytes.length;
      } catch (Exception ex) {
        throw rethrowException(action("reading", file, stream), ex, null);
      } finally {
        closeQuietly(stream);
      }
    }
    private String action(String desc, File file, Object stream) {
      return desc + " " + (file == null ? "stream: " + stream.hashCode() : file.toString());
    }
    public void closeQuietly(Closeable closeable) {
      if (closeable == null)
        return;
      try {
        closeable.close();
      } catch (Throwable ignored) {
      }
    }

  }
}
