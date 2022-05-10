package cell411.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ObservableValue<ValueType> {
//  private final WeakList<ValueObserver<ValueType>> mObservers = new WeakList<>();
  private final ArrayList<ValueObserver<ValueType>> mObservers = new ArrayList<>();
  private       ValueType                           mValue;

  public ObservableValue(ValueType value) {
    mValue = value;
  }

  public ObservableValue() {
    this(null);
  }

  public boolean set(ValueType newValue) {
    if (mValue == newValue) {
      return false;
    }
    if (mValue != null && mValue.equals(newValue)) {
      return false;
    }
    ValueType oldValue = mValue;
    mValue = newValue;
    fireStateChange(newValue, oldValue);
    return true;
  }

  @NonNull public String toString() {
    return "Observable[" + mValue + "]";
  }

  public synchronized void addObserver(ValueObserver<ValueType> observer) {
    mObservers.add(observer);
    ValueType value = get();
  }

  public synchronized void removeObserver(ValueObserver<ValueType> observer) {
    mObservers.remove(observer);
  }

  public ValueType get() {
    return mValue;
  }

  private void fireStateChange(ValueType newValue, ValueType oldValue) {
    ArrayList<ValueObserver<ValueType>> observers;
    synchronized (this) {
      if (mObservers.isEmpty()) {
        return;
      }
      observers = new ArrayList<>(mObservers);
    }
    observers.remove(null);
    for (ValueObserver<ValueType> observer : observers) {
      try {
        // these are weak references, so we need to make sure they
        // did not get nulled out on us.
        if (observer != null) {
          observer.onChange(newValue, oldValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  public int countObservers() {
    return mObservers.size();
  }
}
