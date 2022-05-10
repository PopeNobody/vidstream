package cell411.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cell411.utils.func.Func1;
import cell411.utils.func.Func2;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Collect {
  public static <T> Iterable<T> wrap(Iterator<T> iterator) {
    return () -> iterator;
  }

  public static <T> Iterable<T> wrap(T[] i) {
    return () -> new Iterator<T>() {
      int pos = 0;

      @Override
      public boolean hasNext() {
        return pos < i.length;
      }

      @Override
      public T next() {
        return i[pos++];
      }
    };
  }

  public static <E, T extends E> void addAll(Collection<E> c, Iterator<T> i) {
    addAll(c, wrap(i));
  }

  public static <E, T extends E> void addAll(Collection<E> c, Iterable<T> i) {
    for (T t : i) {
      c.add(t);
    }
  }

  public static <E, T extends E> void addAll(@NonNull Collection<E> c, T[] i) {
    addAll(c, wrap(i));
  }

  public static <E, T extends E> void replaceAll(Collection<E> c, Iterator<T> i)
  {
    c.clear();
    addAll(c, i);
  }

  public static <E, T extends E> void replaceAll(@NonNull Collection<E> c, T[] i) {
    c.clear();
    addAll(c, i);
  }

  public static <E, T extends E> void replaceAll(Collection<E> c, Iterable<T> i)
  {
    c.clear();
    addAll(c, i);
  }

  public static <Type> ArrayList<Type> flatten(ArrayList<List<Type>> listList) {
    return flatten(listList, 20);
  }

  public static <Type> ArrayList<Type> flatten(ArrayList<List<Type>> listList, int skip) {
    ArrayList<Type> res = new ArrayList<>(skip);
    for (List<Type> list : listList) {
      res.addAll(list);
    }
    return res;
  }
  public static <X> List<X> wrapArray(X[] x) {
    return new AbstractList<X>() {
      @Override
      public int size() {
        return x.length;
      }
      @Override
      public X get(int index) {
        return x[index];
      }
    };
  }

  @SafeVarargs
  public static <X> List<X> addAll(@Nullable List<X> list, X... xs) {
    if (list == null) {
      return wrapArray(xs);
    } else {
      list.addAll(Arrays.asList(xs));
      return list;
    }
  }
  public static <K, V> List<K> filterKeys(Map<K, V> map, Func2<Boolean, K, V> pred)
  {
    ArrayList<K> res = new ArrayList<>();
    for(Map.Entry<K,V> entry : map.entrySet()) {
      if(pred.apply(entry.getKey(), entry.getValue())) {
        res.add(entry.getKey());
      }
    }
    return res;
  }
  // This is a funky one.  Given a list of objects, and a list of predicates,
  // return a list of lists of objects, such that each lists
  public static <E, P extends Func1<Boolean, E>>
  List<List<E>> split(List<E> input, List<P> orig)
  {
    if(input.size()==0)
      return new ArrayList<>();

    List<P> predicates = new ArrayList<>(orig);
    Collections.reverse(predicates);
    List<List<E>> lists = new ArrayList<>();

    for(E e : input) {
      int index=0;
      for( P p : predicates) {
        index*=2;
        if(p.apply(e))
          index++;
      }
      List<E> list = lists.get(index);
      if(list==null)
        lists.set(index, new ArrayList<E>());
      lists.get(index).add(e);
    }
    return lists;
  }
  public static <T> T newInstance(Class<T> type) {
    try {
      return type.newInstance();
    } catch ( Throwable t ) {
      throw Util.rethrow("creating new instance of "+type, t);
    }
  }
  @SuppressWarnings("unchecked")
  public static <T> T[] newArray(Class<T> type, int count) {
    return (T[]) Array.newInstance(type, count);
  }
  public static <T> T[] newInstances(Class<T> type, int count) {
    T[] objs = newArray(type,count);
    for(int i=0;i<count;i++){
      objs[i]=newInstance(type);
    }
    return objs;
  }
  public static <T> T[] newInstances(T t, int count) {
    return newInstances(getClass(t), count);
  }

  @SuppressWarnings("unchecked")
  public static <C, D extends C> Class<D> getClass(C t) {
    return (Class<D>) t.getClass();
  }
  public static <T> T newInstance(T oldInstance) {
    Class<T> type = getClass(oldInstance);
    try {
      return type.newInstance();
    } catch (Exception e) {
      throw Util.rethrow("creating new instance like " + oldInstance, e);
    }
  }
  public static <V, C extends Collection<V>>  C filter(C input, Func1<Boolean,V> pred)
  {
    C result = newInstance(input);
    for(V v : input) {
      if(pred.apply(v))
        result.add(v);
    }
    return result;
  }
  static public <T> void merge(List<List<T>> lists, List<List<T>> more)
  {
    for(int i=0;i<lists.size();i++)
      if(lists.get(i)==null)
        lists.set(i,new ArrayList<>());

    while(lists.size()<more.size())
      lists.add(new ArrayList<>());

    for(int i=0;i<more.size();i++){
      lists.get(i).addAll(more.get(i));
    }
  }

  public static class EmptyIterator<XType> implements Iterator<XType> {
    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public XType next() {
      throw new ArrayIndexOutOfBoundsException("Iterated past end");
    }
  }
}
