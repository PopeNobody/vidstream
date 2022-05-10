package cell411.utils;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sachin on 7/16/2015.
 */
public class MD5Util {
  public static String hex(byte[] array)
  {
    PrintString sb = new PrintString();
    for (byte b : array) {
      sb.printf("%02x", (b & 0xff));
    }
    return sb.toString();
  }

  public static String md5Hex(@Nullable String message)
  {
    if (message == null) {
      return null;
    }
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      return hex(md.digest(message.getBytes("CP1252")));
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
      throw new RuntimeException("digesting text", ex);
    }
  }
}

