package cell411.utils;

import androidx.annotation.NonNull;

import cell411.basic.R;
import com.parse.ParseException;



public class LazyMessage {
  public static final String TAG = "LazyMessage";
  Object   mMsg;
  Object[] mArgs;

  public LazyMessage(Object msg, Object... args)
  {
    mMsg = msg;
    mArgs = args;
  }

  @NonNull public String toString()
  {
    if (mMsg instanceof ParseException) {
      ParseException pe = (ParseException) mMsg;
      pe.printStackTrace();
      if (pe.getCode() == 100) {
        mMsg = "Net is a little iffy";
      }
    } else if (mMsg instanceof Exception) {
      Exception e = (Exception) mMsg;
      mMsg = e.getMessage();
    }
    if (!(mMsg instanceof String)) {
      mMsg = String.valueOf(mMsg);
    }
    if (mArgs != null && mArgs.length != 0) {
      mMsg = Util.format((String) mMsg, mArgs);
    }
    return (String) mMsg;
  }
}
