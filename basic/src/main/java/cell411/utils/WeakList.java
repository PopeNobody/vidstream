package cell411.utils;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.WeakHashMap;
// This is a simple list of WeakReferences to objects.  It wraps an ArrayList
// of weak references.  When an object is added, it is wrapped in a weak ref,
// and when it is queried, it dereferences the weak ref.
//
// It uses two functions, ref and deref, to do the transformations.

public class WeakList<Type> extends AbstractList<Type> {
  final ArrayList<WeakReference<Type>> mData;
  WeakHashMap map;

  public WeakList() {
    mData = new ArrayList<>();
  }

  @SuppressWarnings("unused") public WeakList(Iterable<Type> input) {
    mData = new ArrayList<>();
    Iterable<WeakReference<Type>> iterable = new Transformer<Type, WeakReference<Type>>(input) {
    };
    for (WeakReference<Type> value : iterable) {
      mData.add(value);
    }
  }

  @Override public int size() {
    return mData.size();
  }

  @Override public Type get(int index) {
    WeakReference<Type> ref = mData.get(index);
    if (ref == null) {
      return null;
    }
    return ref.get();
  }

  @Override public Type set(int index, Type val) {
    return deref(mData.set(index, ref(val)));
  }

  private WeakReference<Type> ref(Type val) {
    return new WeakReference<>(val);
  }

  private Type deref(WeakReference<Type> ref) {
    return ref == null ? null : ref.get();
  }
  @Override
  public boolean add(Type type) {
    return super.add(type);
  }
  @Override public void add(int index, Type val) {
    mData.add(index, ref(val));
  }

  @Override public Type remove(int index) {
    return deref(mData.remove(index));
  }
}
