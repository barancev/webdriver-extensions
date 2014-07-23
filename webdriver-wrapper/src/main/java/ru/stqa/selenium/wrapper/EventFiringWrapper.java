/*
 * Copyright 2014 Alexei Barantsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.stqa.selenium.wrapper;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class EventFiringWrapper extends WebDriverWrapper {

  private Set<WebDriverListener> listeners = new HashSet<WebDriverListener>();

  public EventFiringWrapper(final WebDriver driver) {
    super(driver);
  }

  public void addListener(WebDriverListener listener) {
    listeners.add(listener);
  }

  public void removeListener(WebDriverListener listener) {
    listeners.remove(listener);
  }

  public void removeAllListeners() {
    listeners.clear();
  }

  @Override
  protected void beforeMethodGlobal(AbstractWrapper target, Method method, Object[] args) {
    for (WebDriverListener listener : listeners) {
      fireBeforeEvent(listener, target, method, args);
    }
    super.beforeMethodGlobal(target, method, args);
  }

  private void fireBeforeEvent(WebDriverListener listener, AbstractWrapper target, Method method, Object[] args) {
    String methodName = createEventMethodName("before", method.getName());

    int argsLength = args != null ? args.length : 0;
    Object[] args2 = new Object[argsLength + 1];
    args2[0] = target.getWrappedOriginal();
    for (int i = 0; i < argsLength; i++) {
      args2[i + 1] = args[i];
    }

    Method m = findMatchingMethod(listener, methodName, args2);
    if (m != null) {
      callListenerMethod(m, listener, args2);
    }
  }

  @Override
  protected void afterMethodGlobal(AbstractWrapper target, Method method, Object res, Object[] args) {
    super.afterMethodGlobal(target, method, res, args);
    for (WebDriverListener listener : listeners) {
      fireAfterEvent(listener, target, method, res, args);
    }
  }

  private void fireAfterEvent(WebDriverListener listener, AbstractWrapper target, Method method, Object res, Object[] args) {
    String methodName = createEventMethodName("after", method.getName());

    boolean isVoid = method.getReturnType() == Void.TYPE;
    int shift = isVoid  ? 0 : 1;

    int argsLength = args != null ? args.length : 0;
    Object[] args2 = new Object[argsLength + 1 + shift];
    args2[0] = target.getWrappedOriginal();
    if (! isVoid) {
      args2[1] = res;
    }
    for (int i = 0; i < argsLength; i++) {
      args2[i + 1 + shift] = args[i];
    }

    Method m = findMatchingMethod(listener, methodName, args2);
    if (m != null) {
      callListenerMethod(m, listener, args2);
    }
  }

  private String createEventMethodName(String prefix, String originalMethodName) {
    return prefix + originalMethodName.substring(0, 1).toUpperCase() + originalMethodName.substring(1);
  }

  private Method findMatchingMethod(WebDriverListener listener, String methodName, Object[] args) {
    for (Method m : listener.getClass().getMethods()) {
      if (m.getName().equals(methodName) && parametersMatch(m, args)) {
        return m;
      }
    }
    return null;
  }

  private boolean parametersMatch(Method m, Object[] args) {
    Class<?>[] params = m.getParameterTypes();
    if (! (params.length == args.length)) {
      return false;
    }
    for (int i = 0; i < params.length; i++) {
      if (! params[0].isAssignableFrom(args[0].getClass())) {
        return false;
      }
    }
    return true;
  }

  private void callListenerMethod(Method m, WebDriverListener listener, Object[] args) {
    try {
      m.invoke(listener, args);
    } catch (IllegalAccessException e) {
      throw new Error("Can't invoke a listener method " + m.getName(), e);
    } catch (InvocationTargetException e) {
      throw new Error("Can't invoke a listener method " + m.getName(), e);
    }
  }
}
