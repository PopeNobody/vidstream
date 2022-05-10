package cell411.utils;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

public interface ExceptionForwarder extends ExceptionHandler {
  default void handleException(@NonNull String note, @NonNull Throwable pe,
                               @Nullable OnCompletionListener listener, Object...args)
  {
    next().handleException(note, pe, listener);
  }

  default void showAlertDialog(String message, OnCompletionListener listener, Object ...args) {
    next().showAlertDialog(message, listener);
  }

  default void showToast(String message, Object...args) {
    next().showToast(message);
  }

  default RuntimeException rethrowException(String s, Exception ex, OnCompletionListener l) {
    handleException(s, ex, l);
    RuntimeException re = new RuntimeException(s, ex);
    if (System.currentTimeMillis() == 0)
      return re;
    throw re;
  }
}
