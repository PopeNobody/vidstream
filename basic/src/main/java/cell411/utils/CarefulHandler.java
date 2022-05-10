package cell411.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class CarefulHandler extends Handler {
  public CarefulHandler(Looper looper) {
    super(looper, new LoggingCallback());
  }

  @Override
  public void dispatchMessage(
    @NonNull
      Message msg)
  {
    try {
      super.dispatchMessage(msg);
    } catch (Throwable t) {
      ExceptionHandler.staticHandleException("Running " + msg.getCallback(), t);
    }
  }

  @Override
  public boolean sendMessageAtTime(
    @NonNull
      Message msg, long uptimeMillis)
  {
    return super.sendMessageAtTime(msg, uptimeMillis);
  }
  public boolean isCurrentThread() {
    return getLooper().isCurrentThread();
  }
}
