package cell411.utils;

public interface OnCompletionListener {
  default void success() {

  }
  default void failure() {

  }

  void done(boolean isSuccess);
}
