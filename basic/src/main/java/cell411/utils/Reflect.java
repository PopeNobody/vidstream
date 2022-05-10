package cell411.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class Reflect {
  public static final String TAG = currentSimpleClassName();

  public static String getTag() {
    return currentSimpleClassName(1);
  }

  public static String currentSimpleClassName() {
    return currentSimpleClassName(1);
  }

  public static String currentMethodName() {
    return currentMethodName(1);
  }

  public static String currentSimpleClassName(int i) {
    String fullClassName = currentClassName(i+1);
    int pos = fullClassName.lastIndexOf('.') + 1;
    return fullClassName.substring(pos);
  }

  public static String currentMethodName(int i) {
    String res = currentStackPos(i+1).getMethodName();
    if (res.equals("<init>")) {
      return currentSimpleClassName(i+1);
    } else {
      return res;
    }
  }

  public static StackTraceElement currentStackPos(int i) {
    Exception ex = new Exception();
    StackTraceElement[] trace = ex.getStackTrace();
    if (trace.length < 3 + i) {
      throw new RuntimeException("trace.length<" + (3 + i) + "!");
    }
    return trace[1 + i];
  }

  public static StackTraceElement currentStackPos() {
    return currentStackPos(1);
  }

  public static Method findStaticMethod(Class<?> clazz, String name) {
    for (Method method : clazz.getDeclaredMethods()) {
      if (!method.getName()
                 .equals(name)) {
        continue;
      }
      if (method.getTypeParameters().length != 0) {
        continue;
      }
      int modifiers = method.getModifiers();
      if ((modifiers & Modifier.STATIC) == 0) {
        continue;
      }
      return method;
    }
    return null;
  }

  public static void announce(boolean b) {
    XLog.i(currentSimpleClassName(1), announceStr(1,b));
  }
  public static String announceStr(int i, Boolean b) {
    StackTraceElement pos = Reflect.currentStackPos(i+1);
    String prefix;
    if(b==null)
      prefix= "X ";
    else if (b)
      prefix="I ";
    else
      prefix="O ";

    return prefix + pos;
  }
  public static void announce(Object obj) {
    String announceStr = announceStr(1, null);
    String objStr = "  " + obj;
    XLog.i(currentSimpleClassName(1), announceStr+": "+objStr);
  }

  public static String announceStr(Boolean b) {
    return announceStr(1,b);
  }

  private static String currentClassName(int i) {
    return currentStackPos(i+1).getClassName();
  }


  public static void stackTrace(PrintString ps, StackTraceElement[] trace) {
    stackTrace(ps, "", trace);
  }

  public static void stackTrace(PrintString ps, String firstLine, StackTraceElement[] trace) {
    if (firstLine != null && !firstLine.isEmpty()) {
      ps.pl(firstLine);
    }
    for (StackTraceElement traceElement : trace) {
      ps.p("\tat ")
        .pl(traceElement);
    }
  }

  public static <Type> Method getMethod(Class<Type> type, String name) {
    try {
      return type.getMethod(name);
    } catch (NoSuchMethodException e) {
      throw Util.rethrow("getting Method "+name+" from "+type, e);
    }
  }
  public static Object invoke(Method isForegroundMethod, Object o) {
    try {
      return isForegroundMethod.invoke(o);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw Util.rethrow("invoking Method "+isForegroundMethod+" on "+o, e);
    }
  }
}

