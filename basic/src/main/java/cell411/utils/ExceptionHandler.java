package cell411.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ExceptionHandler
  extends Thread.UncaughtExceptionHandler
{
  static void staticHandleException(String s, Throwable throwable) {

  }
  static void staticShowAlertDialog(String s) {

  }
  class SystemOutHandler implements ExceptionHandler {
    static ExceptionHandler mNext;

    static ExceptionHandler staticNext() {
      return mNext;
    }
    static void staticSetNext( ExceptionHandler next ){
      mNext=next;
    }
    public ExceptionHandler next() {
      return staticNext();
    }

    public void setNext(ExceptionHandler exceptionHandler) {
      staticSetNext(exceptionHandler);
    }
    @Override
    public void handleException(@NonNull String activity, @NonNull Throwable pe,
                                @Nullable OnCompletionListener listener, Object... args)
    {
      if(mNext==null)
        System.out.println("I should be relieved of duty.  I can't send a toast! Or a dialog!");
      else
        ExceptionHandler.super.handleException(activity,pe,listener,args);
    }
    @Override
    public void showAlertDialog(String message, OnCompletionListener listener, Object... args) {
      if(mNext==null)
        System.out.println("I should be relieved of duty!  I can't show a Dialog!");
      else
        ExceptionHandler.super.showAlertDialog(message, listener, args);
    }
    @Override
    public void showToast(String message, Object... args) {
      if(mNext==null)
        System.out.println("I should be relieved of duty.  I can't send a toast! Or a dialog!");
      else
        ExceptionHandler.super.showToast(message,args);
    }
  }
  default ExceptionHandler next() {
    return SystemOutHandler.staticNext();
  }
  default void setNext(ExceptionHandler next){
    SystemOutHandler.staticSetNext(next);
  }

  default void handleException(@NonNull String activity, @NonNull Throwable pe,
                       @Nullable OnCompletionListener listener, Object...args)
  {
    next().handleException(activity,pe,listener,args);
  }

  default void showAlertDialog(String message, OnCompletionListener listener, Object...args){
    next().showAlertDialog(message,listener,args);
  }

  default void showToast(String message, Object...args) {
    next().showToast(message,args);
  }


  default OnCompletionListener getListener() {
    return null;
  }

  default void handleException(@NonNull String activity, @NonNull Throwable pe) {
    try (PrintString ps = new PrintString()) {
      //noinspection CallToPrintStackTrace
      pe.printStackTrace(ps);
      XLog.i("ExceptionHandler", ps.toString());
      handleException(activity, pe, getListener());
    } catch ( Exception ignored ) {
    }
  }

 @Override
  default void uncaughtException(@NonNull Thread t, @NonNull Throwable e)
  {
    handleException("Running " + t, e);
  }
  default void showToast(int format, Object... args) {
    showToast(Util.format(format, args));
  }

  default void showAlertDialog(String format, Object... args) {
    showAlertDialog(Util.format(format, args), getListener());
  }

  default void showAlertDialog(int format, Object... args) {
    showAlertDialog(Util.format(format, args));
  }
}
