package cell411.utils;

import android.os.HandlerThread;
import android.os.Process;

public class HandlerThreadPlus extends HandlerThread {
  private final static String TAG = Reflect.getTag();
  static int smCount = 0;
  static {
    XLog.i(TAG, Reflect.currentSimpleClassName() + " is loading");
  }
  // This is like a HandlerThread, except it starts itself, and it
  // creates a Handler, as well as a looper.
  //
  // We try to synchronize it so that the handler is created before the
  // constructor returns, allowing you to create a final static field
  // for the Thread, and then another for the Handler.
  protected CarefulHandler mHandler;
  protected boolean        mComplete;
  public HandlerThreadPlus(String name) {
    this(name, MIN_PRIORITY);
  }
  public HandlerThreadPlus(String name, int priority) {
    super(name + " HTP#" + (++smCount), priority);
    long startTime = System.currentTimeMillis();
    start();
    synchronized (this) {
      while (mHandler == null) {
        ThreadUtil.wait(this, 100);
      }
      mComplete = true;
    }
    XLog.i(TAG, "Elapsed: " + (System.currentTimeMillis() - startTime));
  }

  @Override
  protected void onLooperPrepared() {
    cell411.utils.UncaughtExceptionHandler.registerCurrentThread();
    synchronized (this) {
      super.onLooperPrepared();
      mHandler = new CarefulHandler(getLooper());
      ThreadUtil.waitUntil(this, () -> mComplete);
    }
  }

  public synchronized CarefulHandler getHandler() {
    ThreadUtil.waitUntil(this, () -> mHandler != null);
    return mHandler;
  }

  public boolean isCurrentThread() {
    return currentThread() == this;
  }
}
