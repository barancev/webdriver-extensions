/*
 * Copyright 2013 Alexei Barantsev
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package ru.stqa.selenium.wrapper;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractWrapper<T> {

  private final T original;
  private final WebDriverWrapper driverWrapper;

  public AbstractWrapper(final WebDriverWrapper driverWrapper, final T original) {
    this.original = original;
    if (this instanceof WebDriverWrapper) {
      this.driverWrapper = (WebDriverWrapper) this;
    } else {
      this.driverWrapper = driverWrapper;
    }
  }

  public final T getWrappedOriginal() {
    return original;
  }

  public WebDriverWrapper getDriverWrapper() {
    return driverWrapper;
  }

  /**
   * Builds a {@link java.lang.reflect.Proxy} implementing all interfaces of original object. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original object.
   *
   * @param driverWrapper        the underlying driver's wrapper
   * @param original             the underlying original object
   * @param wrapperClass         the class of a wrapper
   */
  public static <T> T wrapOriginal(final WebDriverWrapper driverWrapper, final T original, final Class<? extends AbstractWrapper<T>> wrapperClass) {
    AbstractWrapper<T> wrapper;
    Constructor<? extends AbstractWrapper<T>> constructor = null;
    if (driverWrapper == null) { // top level WebDriverWrapper
      constructor = findMatchingConstructor(wrapperClass, original.getClass());
      if (constructor == null) {
        throw new Error("Wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(original);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }

    } else { // enclosed wrapper
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = findMatchingConstructor(wrapperClass, wrapperClass.getEnclosingClass(), original.getClass());
        } catch (Exception e) {
          throw new Error("Can't create a new wrapper object", e);
        }
      }
      if (constructor == null) {
        try {
          constructor = findMatchingConstructor(wrapperClass, WebDriverWrapper.class, original.getClass());
        } catch (Exception e) {
          throw new Error("Can't create a new wrapper object", e);
        }
      }
      if (constructor == null) {
        throw new Error("Wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, original);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
    }
    return wrapper.wrapOriginal();
  }

  /**
   * Builds a {@link java.lang.reflect.Proxy} implementing all interfaces of original object. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original object.
   */
  public final T wrapOriginal() {
    final T original = getWrappedOriginal();
    final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

    final InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          if (wrapperInterfaces.contains(method.getDeclaringClass())) {
            boolean isUnwrap = method.getName().equals("getWrappedElement");
            if (! isUnwrap) {
              beforeMethod(method, args);
            }
            Object result = callMethod(method, args);
            if (! isUnwrap) {
              afterMethod(method, result, args);
            }
            return result;
          }
          return method.invoke(original, args);
        } catch (InvocationTargetException e) {
          return onError(method, e, args);
        }
      }
    };

    Set<Class<?>> allInterfaces = extractInterfaces(original);
    allInterfaces.addAll(wrapperInterfaces);
    Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

    return (T) Proxy.newProxyInstance(
        this.getClass().getClassLoader(),
        allInterfaces.toArray(allInterfacesArray),
        handler);
  }

  protected void beforeMethod(Method method, Object[] args) {
    getDriverWrapper().beforeMethodGlobal(this, method, args);
  }

  protected Object callMethod(Method method, Object[] args) throws Throwable {
    return getDriverWrapper().callMethodGlobal(this, method, args);
  }

  protected void afterMethod(Method method, Object res, Object[] args) {
    getDriverWrapper().afterMethodGlobal(this, method, res, args);
  }

  protected Object onError(Method method, InvocationTargetException e, Object[] args) throws Throwable {
    return getDriverWrapper().onErrorGlobal(this, method, e, args);
  }

  private static <T> Constructor<? extends AbstractWrapper<T>> findMatchingConstructor(
      Class<? extends AbstractWrapper<T>> wrapperClass, Class<?>... classes)
  {
    for (Constructor<?> ctor : wrapperClass.getConstructors()) {
      if (isMatchingConstructor(ctor, classes)) {
        return (Constructor<? extends AbstractWrapper<T>>) ctor;
      }
    }
    return null;
  }

  private static boolean isMatchingConstructor(Constructor<?> ctor, Class<?>... classes) {
    Class<?>[] parameterTypes = ctor.getParameterTypes();
    if (parameterTypes.length != classes.length) {
      return false;
    }
    for (int i = 0; i < parameterTypes.length; i++) {
      if (! parameterTypes[i].isAssignableFrom(classes[i])) {
        return false;
      }
    }
    return true;
  }

  private static Set<Class<?>> extractInterfaces(final Object object) {
    return extractInterfaces(object.getClass());
  }

  private static Set<Class<?>> extractInterfaces(final Class<?> clazz) {
    Set<Class<?>> allInterfaces = new HashSet<Class<?>>();
    extractInterfaces(allInterfaces, clazz);

    return allInterfaces;
  }

  private static void extractInterfaces(final Set<Class<?>> collector, final Class<?> clazz) {
    if (clazz == null || Object.class.equals(clazz)) {
      return;
    }

    final Class<?>[] classes = clazz.getInterfaces();
    for (Class<?> interfaceClass : classes) {
      collector.add(interfaceClass);
      for (Class<?> superInterface : interfaceClass.getInterfaces()) {
        collector.add(superInterface);
        extractInterfaces(collector, superInterface);
      }
    }
    extractInterfaces(collector, clazz.getSuperclass());
  }

  @Override
  public String toString() {
    return "Wrapper for {" + original + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o instanceof AbstractWrapper) {
      AbstractWrapper that = (AbstractWrapper) o;
      return original.equals(that.original);

    } else {
      return this.original.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return original.hashCode();
  }
}
