package cell411.utils;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
  final T[] mList;
  int mPos;

  public ArrayIterator(T[] list)
  {
    mList = list;
    mPos = 0;
  }

  @Override public boolean hasNext()
  {
    return mPos < mList.length;
  }

  Iterable<T> test()
  {
    return null;
  }

  @Override public T next()
  {
    if (!hasNext()) {
      throw new ArrayIndexOutOfBoundsException("Iterated off a cliff!");
    }
    return mList[mPos++];
  }
}
