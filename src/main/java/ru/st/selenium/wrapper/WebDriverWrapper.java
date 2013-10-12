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
package ru.st.selenium.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

/**
 * Simple {@link WrapsDriver} delegating all calls to the wrapped driver and providing facility to
 * wrap returned {@link WebElement}. This class allows to easily extend WebDriver by adding
 * new functionality to a wrapper. Instantiation should not happen directly but rather with the
 * help of a dynamic proxy to respect the interfaces implemented by the wrapped driver.
 * Example:
 * <code><pre>
 * WebDriver wrapped = WebDriverWrapper.buildWrapper(driver, MyWebDriverWrapper.class);
 * </pre></code>
 * or
 * <code><pre>
 * MyWebDriverWrapper wrapper = new MyWebDriverWrapper(driver, otherParameter);
 * WebDriver wrapped = WebDriverWrapper.buildWrapper(driver, wrapper);
 * </pre></code>
 */
public abstract class WebDriverWrapper implements WebDriver, WrapsDriver {

  private final WebDriver wrappedDriver;

  public WebDriverWrapper(final WebDriver driver) {
    wrappedDriver = driver;
  }

  public WebDriver getWrappedDriver() {
    return wrappedDriver;
  }

  /**
   * Facility to wrap elements returned by {@link #findElement(By)} and {@link #findElements(By)}
   * from this instance (as well as from {@link WebElementWrapper} when using it).
   *
   * @param element the original element
   * @return the element visible to the caller of {@link #findElement(By)}/{@link
   *         #findElements(By)}. The default behavior is to return the original element.
   */
  protected WebElement wrapElement(final WebElement element) {
    return element;
  }

  /**
   * Facility to wrap elements returned by {@link #findElements(By)} from this instance (as well
   * as from {@link WebElementWrapper} when using it).
   *
   * @param elements the original list of elements
   * @return the default behavior is to call {@link #wrapElement(WebElement)} for each element.
   */
  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (final ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  public void get(String url) {
    wrappedDriver.get(url);
  }

  public String getCurrentUrl() {
    return wrappedDriver.getCurrentUrl();
  }

  public String getTitle() {
    return wrappedDriver.getTitle();
  }

  public List<WebElement> findElements(final By by) {
    return wrapElements(wrappedDriver.findElements(by));
  }

  public WebElement findElement(final By by) {
    return wrapElement(wrappedDriver.findElement(by));
  }

  public String getPageSource() {
    return wrappedDriver.getPageSource();
  }

  public void close() {
    wrappedDriver.close();
  }

  public void quit() {
    wrappedDriver.quit();
  }

  public Set<String> getWindowHandles() {
    return wrappedDriver.getWindowHandles();
  }

  public String getWindowHandle() {
    return wrappedDriver.getWindowHandle();
  }

  public TargetLocator switchTo() {
    return wrappedDriver.switchTo();
  }

  public Navigation navigate() {
    return wrappedDriver.navigate();
  }

  public Options manage() {
    return wrappedDriver.manage();
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original.
   *
   * @param driver               the underlying driver
   * @param wrapperClass         the class of a wrapper
   */
  public static WebDriver buildWrapper(final WebDriver driver, final Class<? extends WebDriverWrapper> wrapperClass) {
    WebDriverWrapper wrapper = null;
    try {
      wrapper = wrapperClass.getConstructor(WebDriver.class).newInstance(driver);
    } catch (InstantiationException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (IllegalAccessException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (InvocationTargetException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (NoSuchMethodException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return buildWrapper(driver, wrapper);
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original.
   *
   * @param driver               the wrapped driver
   * @param wrapper              the object wrapping the driver
   */
  public static WebDriver buildWrapper(final WebDriver driver, final WebDriverWrapper wrapper) {
    final Set<Class<?>> wrapperInterfaces = extractInterfaces(wrapper);

    final InvocationHandler handler = new InvocationHandler() {
      public Object invoke(final Object proxy, final Method method,
                           final Object[] args) throws Throwable {
        try {
          if (wrapperInterfaces.contains(method.getDeclaringClass())) {
            return method.invoke(wrapper, args);
          }
          return method.invoke(driver, args);
        } catch (final InvocationTargetException e) {
          throw e.getTargetException();
        }
      }
    };

    final Set<Class<?>> allInterfaces = extractInterfaces(driver);
    allInterfaces.addAll(wrapperInterfaces);
    final Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

    return (WebDriver) Proxy.newProxyInstance(
        wrapper.getClass().getClassLoader(),
        allInterfaces.toArray(allInterfacesArray),
        handler);
  }

  private static Set<Class<?>> extractInterfaces(final Object object) {
    return extractInterfaces(object.getClass());
  }

  private static Set<Class<?>> extractInterfaces(final Class<?> clazz) {
    final Set<Class<?>> allInterfaces = new HashSet<Class<?>>();
    extractInterfaces(allInterfaces, clazz);

    return allInterfaces;
  }

  private static void extractInterfaces(final Set<Class<?>> collector, final Class<?> clazz) {
    if (clazz == null || Object.class.equals(clazz)) {
      return; // Done
    }

    final Class<?>[] classes = clazz.getInterfaces();
    for (final Class<?> interfaceClass : classes) {
      collector.add(interfaceClass);
      for (final Class<?> superInterface : interfaceClass.getInterfaces()) {
        collector.add(superInterface);
        extractInterfaces(collector, superInterface);
      }
    }
    extractInterfaces(collector, clazz.getSuperclass());
  }
}