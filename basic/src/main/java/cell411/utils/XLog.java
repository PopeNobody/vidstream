package cell411.utils;

import android.util.Log;

import java.io.PrintStream;

import static android.util.Log.*;

@SuppressWarnings("unused")
public final class XLog {

  static ILog        smLog = new StreamLog();

  private XLog() {
  }

  public static boolean isLoggable(String tag, int level) {
    return smLog.isLoggable(tag, level);
  }

  public static int d(String tag, LazyMessage msg) {
    if (!isLoggable(tag, DEBUG)) {
      return 0;
    }
    return smLog.d(tag, msg.toString(), null);
  }

  public static int d(String tag, String msg, Throwable tr, Object... args) {
    if (!isLoggable(tag, DEBUG)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.v(tag, msg, tr);
  }

  public static int d(String tag, String msg, Object... args) {
    if (!isLoggable(tag, DEBUG)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.d(tag, msg, null);
  }

  public static int e(String tag, LazyMessage msg) {
    if (!isLoggable(tag, ERROR)) {
      return 0;
    }
    return smLog.e(tag, msg.toString(), null);
  }

  public static int e(String tag, String msg, Object... args) {
    if (!isLoggable(tag, ERROR)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.e(tag, msg, null);
  }

  public static int e(String tag, String msg, Throwable tr, Object... args) {
    if (!isLoggable(tag, ERROR)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.e(tag, msg, tr);
  }

  public static int i(String tag, LazyMessage msg) {
    int res = 0;
    if (isLoggable(tag, INFO)) {
      res = smLog.i(tag, msg.toString(), null);
    }
    return res;
  }

  public static int i(String tag, String msg, Object... args) {
    if (!isLoggable(tag, INFO)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.i(tag, msg, null);
  }

  public static int i(String tag, String msg, Throwable tr, Object... args) {
    if (!isLoggable(tag, INFO)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.i(tag, msg, tr);
  }

  public static int v(String tag, LazyMessage msg) {
    int res = 0;
    if (isLoggable(tag, VERBOSE)) {
      res = smLog.v(tag, msg.toString(), null);
    }
    return res;
  }

  public static int v(String tag, String msg, Object... args) {
    if (!isLoggable(tag, VERBOSE)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.v(tag, msg, null);
  }

  public static int v(String tag, String msg, Throwable t, Object... args) {
    if (!isLoggable(tag, VERBOSE)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.v(tag, msg, t);
  }

  public static int w(String tag, LazyMessage msg) {
    if (!isLoggable(tag, WARN)) {
      return 0;
    }
    return smLog.w(tag, msg.toString(), null);
  }

  public static int w(String tag, String msg, Object... args) {
    if (!isLoggable(tag, WARN)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.w(tag, msg, null);
  }

  public static int w(String tag, String msg, Throwable tr, Object... args) {
    if (!isLoggable(tag, WARN)) {
      return 0;
    }
    if (args != null && args.length > 0) {
      msg = Util.format(msg, args);
    }
    return smLog.w(tag, msg, tr);
  }

  public static int w(String tag, Throwable tr) {
    if (!isLoggable(tag, WARN)) {
      return 0;
    }
    return smLog.w(tag, "exception", tr);
  }

  public interface ILogUser extends ILog {
    default int d(String tag, String msg, Throwable tr){
      return XLog.d(tag,msg,tr);
    }  default int e(String tag, String msg, Throwable tr){
      return XLog.e(tag,msg,tr);
    }
    default int i(String tag, String msg, Throwable tr){
      return XLog.i(tag,msg,tr);
    }
    default int v(String tag, String msg, Throwable tr){
      return XLog.v(tag,msg,tr);
    }
    default int w(String tag, String msg, Throwable tr){
      return XLog.w(tag,msg,tr);
    }
  }

  public interface ILog {
    boolean isLoggable(String tag, int level);

    int d(String tag, String msg, Throwable tr);

    int e(String tag, String msg, Throwable tr);

    int i(String tag, String msg, Throwable tr);

    int v(String tag, String msg, Throwable t);

    int w(String tag, String msg, Throwable tr);
  }

  static final class StreamLog implements ILog {
    private final PrintStream mStream = System.out;

    @Override public boolean isLoggable(String tag, int level) {
      return true;
    }

    @Override public int d(String tag, String msg, Throwable tr) {
      mStream.println("D/" + tag + ": " + msg);
      if (tr != null) {
        mStream.println(Log.getStackTraceString(tr));
      }

      return 0;
    }

    @Override public int e(String tag, String msg, Throwable tr) {
      mStream.println("E/" + tag + ": " + msg);
      if (tr != null) {
        mStream.println(Log.getStackTraceString(tr));
      }

      return 0;
    }

    @Override public int i(String tag, String msg, Throwable tr) {
      mStream.println("I/" + tag + ": " + msg);
      if (tr != null) {
        mStream.println(Log.getStackTraceString(tr));
      }
      return 0;
    }

    @Override public int v(String tag, String msg, Throwable tr) {
      mStream.println("V/" + tag + ": " + msg);
      if (tr != null) {
        mStream.println(Log.getStackTraceString(tr));
      }
      return 0;
    }

    @Override public int w(String tag, String msg, Throwable tr) {
      mStream.println("W/" + tag + ": " + msg);
      if (tr != null) {
        mStream.println(Log.getStackTraceString(tr));
      }
      return 0;
    }
  }

  // This ILog just forwards whatever it gets to the Android log.
  // However, one could implement the same interface and do something
  // totally different.
  static final class AndroidLog implements ILog {
    public boolean isLoggable(String tag, int level) {
      return false;
    }

    public int d(String tag, String msg, Throwable tr) {
      return Log.v(tag, msg, tr);
    }

    public int e(String tag, String msg, Throwable tr) {
      return Log.e(tag, msg, tr);
    }

    public int i(String tag, String msg, Throwable tr) {
      return Log.i(tag, msg, tr);
    }

    public int v(String tag, String msg, Throwable tr) {
      return Log.v(tag, msg, tr);
    }

    public int w(String tag, String msg, Throwable tr) {
      return Log.w(tag, msg, tr);
    }
  }
}
