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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;

/**
 * Simple {@link WrapsDriver} delegating all calls to the wrapped driver and providing facility to
 * wrap returned {@link WebElement}. This class allows to easily extend WebDriver by adding
 * new functionality to a wrapper. Instantiation should not happen directly but rather with the
 * help of a dynamic proxy to respect the interfaces implemented by the wrapped driver.
 * Example:
 * <code><pre>
 * WebDriver wrapped = WebDriverWrapper.wrapDriver(driver, MyWebDriverWrapper.class);
 * </pre></code>
 * or
 * <code><pre>
 * MyWebDriverWrapper wrapper = new MyWebDriverWrapper(driver, otherParameter);
 * WebDriver wrapped = WebDriverWrapper.wrapDriver(driver, wrapper);
 * </pre></code>
 */
public abstract class WebDriverWrapper implements WebDriver, WrapsDriver {

  private final WebDriver wrappedDriver;

  public WebDriverWrapper(WebDriver driver) {
    wrappedDriver = driver;
  }

  @Override
  public WebDriver getWrappedDriver() {
    return wrappedDriver;
  }

  @Override
  public void get(String url) {
    getWrappedDriver().get(url);
  }

  @Override
  public String getCurrentUrl() {
    return getWrappedDriver().getCurrentUrl();
  }

  @Override
  public String getTitle() {
    return getWrappedDriver().getTitle();
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
    return WebElementWrapper.wrapElement(this, element, getElementWrapperClass());
  }
  
  protected Class<? extends WebElementWrapper> getElementWrapperClass() {
    return WebElementWrapper.class;
  }
  
  /**
   * Facility to wrap elements returned by {@link #findElements(By)} from this instance (as well
   * as from {@link WebElementWrapper} when using it).
   *
   * @param elements the original list of elements
   * @return the default behavior is to call {@link #wrapElement(WebElement)} for each element.
   */
  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return wrapElements(getWrappedDriver().findElements(by));
  }

  @Override
  public WebElement findElement(final By by) {
    return wrapElement(getWrappedDriver().findElement(by));
  }

  @Override
  public String getPageSource() {
    return getWrappedDriver().getPageSource();
  }

  @Override
  public void close() {
    getWrappedDriver().close();
  }

  @Override
  public void quit() {
    getWrappedDriver().quit();
  }

  @Override
  public Set<String> getWindowHandles() {
    return getWrappedDriver().getWindowHandles();
  }

  @Override
  public String getWindowHandle() {
    return getWrappedDriver().getWindowHandle();
  }

  @Override
  public TargetLocator switchTo() {
    return getWrappedDriver().switchTo();
  }

  @Override
  public Navigation navigate() {
    return getWrappedDriver().navigate();
  }

  @Override
  public Options manage() {
    return getWrappedDriver().manage();
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   *
   * @param driver               the underlying driver
   * @param wrapperClass         the class of a wrapper
   */
  public static WebDriver wrapDriver(final WebDriver driver, final Class<? extends WebDriverWrapper> wrapperClass) {
    WebDriverWrapper wrapper = null;
    try {
      wrapper = wrapperClass.getConstructor(WebDriver.class).newInstance(driver);
    } catch (NoSuchMethodException e) {
      throw new Error("Wrapper class should provide a constructor with a single WebDriver parameter", e);
    } catch (Exception e) {
      throw new Error("Can't create a new wrapper object", e);
    }
    return wrapDriver(driver, wrapper);
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   *
   * @param driver               the wrapped driver
   * @param wrapper              the object wrapping the driver
   */
  public static WebDriver wrapDriver(final WebDriver driver, final WebDriverWrapper wrapper) {
    final Set<Class<?>> wrapperInterfaces = extractInterfaces(wrapper);

    final InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          if (wrapperInterfaces.contains(method.getDeclaringClass())) {
            wrapper.beforeMethod(method, args);
            Object result = wrapper.callMethod(method, args);
            wrapper.afterMethod(method, result, args);
            return result;
          }
          return method.invoke(driver, args);
        } catch (InvocationTargetException e) {
          wrapper.onError(method, e, args);
          throw e.getTargetException();
        }
      }
    };

    Set<Class<?>> allInterfaces = extractInterfaces(driver);
    allInterfaces.addAll(wrapperInterfaces);
    Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

    return (WebDriver) Proxy.newProxyInstance(
        wrapper.getClass().getClassLoader(),
        allInterfaces.toArray(allInterfacesArray),
        handler);
  }

  protected void beforeMethod(Method method, Object[] args) {
  }

  protected Object callMethod(Method method, Object[] args) throws Throwable {
    return method.invoke(this, args);
  }

  protected void afterMethod(Method method, Object res, Object[] args) {
  }

  protected void onError(Method method, InvocationTargetException e, Object[] args) {
  }

  /**
   * Simple {@link WrapsElement} delegating all calls to the wrapped {@link WebElement}.
   * The methods {@link WebDriverWrapper#wrapElement(WebElement)}/{@link WebDriverWrapper#wrapElements(List<WebElement>)} will
   * be called on the related {@link WebDriverWrapper} to wrap the elements returned by {@link #findElement(By)}/{@link #findElements(By)}.
   */
  public static class WebElementWrapper implements WebElement, WrapsElement {

    private final WebElement wrappedElement;
    private final WebDriverWrapper driverWrapper;

    public WebElementWrapper(final WebDriverWrapper driverWrapper, final WebElement element) {
      wrappedElement = element;
      this.driverWrapper = driverWrapper;
    }

    @Override
    public WebElement getWrappedElement() {
      return wrappedElement;
    }

    /**
     * Get the related {@link WebDriverWrapper} that will be used to wrap elements.
     */
    protected WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public void click() {
      getWrappedElement().click();
    }

    @Override
    public void submit() {
      getWrappedElement().submit();
    }

    @Override
    public void sendKeys(final CharSequence... keysToSend) {
      getWrappedElement().sendKeys(keysToSend);
    }

    @Override
    public void clear() {
      getWrappedElement().clear();
    }

    @Override
    public String getTagName() {
      return getWrappedElement().getTagName();
    }

    @Override
    public String getAttribute(final String name) {
      return getWrappedElement().getAttribute(name);
    }

    @Override
    public boolean isSelected() {
      return getWrappedElement().isSelected();
    }

    @Override
    public boolean isEnabled() {
      return getWrappedElement().isEnabled();
    }

    @Override
    public String getText() {
      return getWrappedElement().getText();
    }

    @Override
    public List<WebElement> findElements(final By by) {
      final List<WebElement> elements = getWrappedElement().findElements(by);
      return getDriverWrapper().wrapElements(elements);
    }

    @Override
    public WebElement findElement(final By by) {
      return getDriverWrapper().wrapElement(getWrappedElement().findElement(by));
    }

    @Override
    public boolean isDisplayed() {
      return getWrappedElement().isDisplayed();
    }

    @Override
    public Point getLocation() {
      return getWrappedElement().getLocation();
    }

    @Override
    public Dimension getSize() {
      return getWrappedElement().getSize();
    }

    @Override
    public String getCssValue(final String propertyName) {
      return getWrappedElement().getCssValue(propertyName);
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original element. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original element.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param element              the underlying element
     * @param wrapperClass         the class of a wrapper
     */
    public static WebElement wrapElement(final WebDriverWrapper driverWrapper, final WebElement element, final Class<? extends WebElementWrapper> wrapperClass) {
      WebElementWrapper wrapper = null;
      try {
        wrapper = wrapperClass.getConstructor(WebDriverWrapper.class, WebElement.class).newInstance(driverWrapper, element);
      } catch (NoSuchMethodException e) {
        throw new Error("Wrapper class should provide a constructor with WebDriverWrapper and WebElement parameters", e);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapElement(driverWrapper, element, wrapper);
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original driver.
     *
     * @param driver               the wrapped driver
     * @param wrapper              the object wrapping the driver
     */
    public static WebElement wrapElement(final WebDriverWrapper driverWrapper, final WebElement element, final WebElementWrapper wrapper) {
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(wrapper);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              wrapper.beforeMethod(method, args);
              Object result = wrapper.callMethod(method, args);
              wrapper.afterMethod(method, result, args);
              return result;
            }
            return method.invoke(element, args);
          } catch (InvocationTargetException e) {
            wrapper.onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(element);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (WebElement) Proxy.newProxyInstance(
          wrapper.getClass().getClassLoader(),
          allInterfaces.toArray(allInterfacesArray),
          handler);
    }

    protected void beforeMethod(Method method, Object[] args) {
    }

    protected Object callMethod(Method method, Object[] args) throws Throwable {
      return method.invoke(this, args);
    }

    protected void afterMethod(Method method, Object res, Object[] args) {
    }

    protected void onError(Method method, InvocationTargetException e, Object[] args) {
    }
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

}