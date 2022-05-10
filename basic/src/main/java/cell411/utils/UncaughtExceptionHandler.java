package cell411.utils;

public class UncaughtExceptionHandler {
  public static final String TAG = Reflect.getTag();

  public static void registerCurrentThread() {
    Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler::uncaughtException);
  }

  public static void uncaughtException(Thread thread, Throwable throwable) {
    XLog.e(TAG, "uncaught exception in thread " + thread);
    XLog.e(TAG, "  throwable: " + throwable);
    ExceptionHandler.staticHandleException("running thread " + thread, throwable);
  }
}
