package cell411.utils;


public abstract class Command implements Runnable, ExceptionForwarder {
  final         String               mName;
  private final OnCompletionListener mCallback;
  public Command(String name) {
    this(name,null);
  }
  public Command(String name, OnCompletionListener callback) {
    mName        = name;
    mCallback    = callback;
  }
  public String getName() {
    return mName;
  }
  @Override
  public final void run() {
    try {
      boolean result = execute();
      if (mCallback != null)
        mCallback.done(result);
    } catch (Throwable t) {
      handleException("running " + getName(), t);
      mCallback.done(false);
    }
  }
  public abstract boolean execute() throws Exception;
}
