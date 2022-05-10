package cell411.utils;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends OutputStream {
  final OutputStream mStream1;
  final OutputStream mStream2;

  public TeeOutputStream(OutputStream stream1, OutputStream stream2) {
    mStream1 = stream1;
    mStream2 = stream2;
  }

  @Override public void flush() throws IOException {
    try {
      mStream1.flush();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    try {
      mStream2.flush();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  @Override public void close() throws IOException {
    try {
      mStream1.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    try {
      mStream2.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  @Override public void write(int b) throws IOException {
    try {
      mStream1.write(b);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    try {
      mStream2.write(b);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
