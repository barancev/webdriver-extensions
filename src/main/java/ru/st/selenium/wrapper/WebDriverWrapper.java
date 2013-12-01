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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.security.Credentials;

/**
 * This class allows to extend WebDriver by adding new functionality to a wrapper.
 * Example of use:
 * <code><pre>
 * WebDriver driver = WebDriverWrapper.wrapDriver(originalDriver, MyWebDriverWrapper.class);
 * </pre></code>
 * or
 * <code><pre>
 * MyWebDriverWrapper wrapper = new MyWebDriverWrapper(originalDriver, otherParameter);
 * WebDriver driver = wrapper.getDriver();
 * </pre></code>
 */
public class WebDriverWrapper implements WebDriver, WrapsDriver, JavascriptExecutor {

  private final WebDriver originalDriver;
  private WebDriver enhancedDriver = null;

  public WebDriverWrapper(WebDriver driver) {
    originalDriver = driver;
  }

  protected Class<? extends WebElementWrapper> getElementWrapperClass() {
    return WebElementWrapper.class;
  }

  protected WebElement wrapElement(final WebElement element) {
    return WebElementWrapper.wrapElement(this, element, getElementWrapperClass());
  }

  private List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  protected Class<? extends TargetLocatorWrapper> getTargetLocatorWrapperClass() {
    return TargetLocatorWrapper.class;
  }

  private TargetLocator wrapTargetLocator(final TargetLocator targetLocator) {
    return TargetLocatorWrapper.wrapTargetLocator(this, targetLocator, getTargetLocatorWrapperClass());
  }

  protected Class<? extends AlertWrapper> getAlertWrapperClass() {
    return AlertWrapper.class;
  }

  private Alert wrapAlert(final Alert alert) {
    return AlertWrapper.wrapAlert(this, alert, getAlertWrapperClass());
  }

  protected Class<? extends NavigationWrapper> getNavigationWrapperClass() {
    return NavigationWrapper.class;
  }

  private Navigation wrapNavigation(final Navigation navigator) {
    return NavigationWrapper.wrapNavigation(this, navigator, getNavigationWrapperClass());
  }

  protected Class<? extends OptionsWrapper> getOptionsWrapperClass() {
    return OptionsWrapper.class;
  }

  private Options wrapOptions(final Options options) {
    return OptionsWrapper.wrapOptions(this, options, getOptionsWrapperClass());
  }

  protected Class<? extends TimeoutsWrapper> getTimeoutsWrapperClass() {
    return TimeoutsWrapper.class;
  }

  private Timeouts wrapTimeouts(final Timeouts timeouts) {
    return TimeoutsWrapper.wrapTimeouts(this, timeouts, getTimeoutsWrapperClass());
  }

  protected Class<? extends WindowWrapper> getWindowWrapperClass() {
    return WindowWrapper.class;
  }

  private Window wrapWindow(final Window window) {
    return WindowWrapper.wrapWindow(this, window, getWindowWrapperClass());
  }

  protected Class<? extends CoordinatesWrapper> getCoordinatesWrapperClass() {
    return CoordinatesWrapper.class;
  }

  private Coordinates wrapCoordinates(final Coordinates coordinates) {
    return CoordinatesWrapper.wrapCoordinates(this, coordinates, getCoordinatesWrapperClass());
  }

  // TODO: implement proper wrapping for arbitrary objects
  private Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return wrapElement((WebElement) object);
    } else {
      return object;
    }
  }

  @Override
  public final WebDriver getWrappedDriver() {
    return originalDriver;
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

  @Override
  public WebElement findElement(final By by) {
    return wrapElement(getWrappedDriver().findElement(by));
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return wrapElements(getWrappedDriver().findElements(by));
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
    return wrapTargetLocator(getWrappedDriver().switchTo());
  }

  @Override
  public Navigation navigate() {
    return wrapNavigation(getWrappedDriver().navigate());
  }

  @Override
  public Options manage() {
    return wrapOptions(getWrappedDriver().manage());
  }

  @Override
  public Object executeScript(String script, Object... args) {
    WebDriver driver = getWrappedDriver();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    WebDriver driver = getWrappedDriver();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeAsyncScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   *
   * @param driver               the underlying driver
   * @param wrapperClass         the class of a wrapper
   */
  public final static WebDriver wrapDriver(final WebDriver driver, final Class<? extends WebDriverWrapper> wrapperClass) {
    WebDriverWrapper wrapper = null;
    try {
      wrapper = wrapperClass.getConstructor(WebDriver.class).newInstance(driver);
    } catch (NoSuchMethodException e) {
      throw new Error("Wrapper class should provide a constructor with a single WebDriver parameter", e);
    } catch (Exception e) {
      throw new Error("Can't create a new wrapper object", e);
    }
    return wrapper.getDriver();
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   */
  public final WebDriver getDriver() {
    if (enhancedDriver != null) {
      return enhancedDriver;
    }

    final WebDriver driver = getWrappedDriver();
    final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

    final InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          if (wrapperInterfaces.contains(method.getDeclaringClass())) {
            beforeMethod(method, args);
            Object result = callMethod(method, args);
            afterMethod(method, result, args);
            return result;
          }
          return method.invoke(driver, args);
        } catch (InvocationTargetException e) {
          onError(method, e, args);
          throw e.getTargetException();
        }
      }
    };

    Set<Class<?>> allInterfaces = extractInterfaces(driver);
    allInterfaces.addAll(wrapperInterfaces);
    Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

    enhancedDriver = (WebDriver) Proxy.newProxyInstance(
        this.getClass().getClassLoader(),
        allInterfaces.toArray(allInterfacesArray),
        handler);

    return enhancedDriver;
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

    private final WebElement originalElement;
    private final WebDriverWrapper driverWrapper;

    public WebElementWrapper(final WebDriverWrapper driverWrapper, final WebElement element) {
      originalElement = element;
      this.driverWrapper = driverWrapper;
    }

    @Override
    public final WebElement getWrappedElement() {
      return originalElement;
    }

    private WebDriverWrapper getDriverWrapper() {
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
      return getDriverWrapper().wrapElements(getWrappedElement().findElements(by));
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

    public Coordinates getCoordinates() {
      Locatable locatable = (Locatable) getWrappedElement();
      return getDriverWrapper().wrapCoordinates(locatable.getCoordinates());
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original element. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original element.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param element              the underlying element
     * @param wrapperClass         the class of a wrapper
     */
    public final static WebElement wrapElement(final WebDriverWrapper driverWrapper, final WebElement element, final Class<? extends WebElementWrapper> wrapperClass) {
      WebElementWrapper wrapper = null;
      Constructor<? extends WebElementWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), WebElement.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, WebElement.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, element);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.getElement();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original element. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original element.
     */
    public final WebElement getElement() {
      final WebElement element = getWrappedElement();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(element, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(element);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (WebElement) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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
  
  public static class TargetLocatorWrapper implements TargetLocator {
  
    private final TargetLocator originalTargetLocator;
    private final WebDriverWrapper driverWrapper;

    public TargetLocatorWrapper(final WebDriverWrapper driverWrapper, final TargetLocator targetLocator) {
      originalTargetLocator = targetLocator;
      this.driverWrapper = driverWrapper;
    }

    public final TargetLocator getWrappedTargetLocator() {
      return originalTargetLocator;
    }

    private final WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public WebDriver frame(int frameIndex) {
      getWrappedTargetLocator().frame(frameIndex);
      return getDriverWrapper().getDriver();
    }
  
    @Override
    public WebDriver frame(String frameName) {
      getWrappedTargetLocator().frame(frameName);
      return getDriverWrapper().getDriver();
    }
  
    @Override
    public WebDriver frame(WebElement frameElement) {
      getWrappedTargetLocator().frame(frameElement);
      return getDriverWrapper().getDriver();
    }
  
    @Override
    public WebDriver window(String windowName) {
      getWrappedTargetLocator().window(windowName);
      return getDriverWrapper().getDriver();
    }
  
    @Override
    public WebDriver defaultContent() {
      getWrappedTargetLocator().defaultContent();
      return getDriverWrapper().getDriver();
    }
  
    @Override
    public WebElement activeElement() {
      return getDriverWrapper().wrapElement(getWrappedTargetLocator().activeElement());
    }
  
    @Override
    public Alert alert() {
        return getDriverWrapper().wrapAlert(getWrappedTargetLocator().alert());
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original target locator. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original target locator.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param targetLocator        the underlying target locator
     * @param wrapperClass         the class of a wrapper
     */
    public final static TargetLocator wrapTargetLocator(final WebDriverWrapper driverWrapper, final TargetLocator targetLocator, final Class<? extends TargetLocatorWrapper> wrapperClass) {
      TargetLocatorWrapper wrapper = null;
      Constructor<? extends TargetLocatorWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), TargetLocator.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, TargetLocator.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, targetLocator);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.getTargetLocator();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original target locator. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original target locator.
     */
    public final TargetLocator getTargetLocator() {
      final TargetLocator targetLocator = getWrappedTargetLocator();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(targetLocator, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(targetLocator);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (TargetLocator) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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
  
  public static class NavigationWrapper implements Navigation {
    
    private final Navigation originalNavigator;
    private final WebDriverWrapper driverWrapper;

    public NavigationWrapper(final WebDriverWrapper driverWrapper, final Navigation navigator) {
      originalNavigator = navigator;
      this.driverWrapper = driverWrapper;
    }

    public final Navigation getWrappedNavigation() {
      return originalNavigator;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public void to(String url) {
      getWrappedNavigation().to(url);
    }

    @Override
    public void to(URL url) {
      getWrappedNavigation().to(url);
    }

    @Override
    public void back() {
      getWrappedNavigation().back();
    }

    @Override
    public void forward() {
      getWrappedNavigation().forward();
    }

    @Override
    public void refresh() {
      getWrappedNavigation().refresh();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original navigator. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original navigator.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param navigator            the underlying navigator
     * @param wrapperClass         the class of a wrapper
     */
    public final static Navigation wrapNavigation(final WebDriverWrapper driverWrapper, final Navigation navigator, final Class<? extends NavigationWrapper> wrapperClass) {
      NavigationWrapper wrapper = null;
      Constructor<? extends NavigationWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Navigation.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Navigation.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, navigator);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapNavigation();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original navigator. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original navigator.
     */
    public final Navigation wrapNavigation() {
      final Navigation navigator = getWrappedNavigation();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(navigator, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(navigator);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Navigation) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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
  
  public static class AlertWrapper implements Alert {

    private final Alert originalAlert;
    private final WebDriverWrapper driverWrapper;

    public AlertWrapper(final WebDriverWrapper driverWrapper, final Alert alert) {
      originalAlert = alert;
      this.driverWrapper = driverWrapper;
    }

    public final Alert getWrappedAlert() {
      return originalAlert;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public void accept() {
      getWrappedAlert().accept();
    }

    @Override
    @Beta
    public void authenticateUsing(Credentials creds) {
      getWrappedAlert().authenticateUsing(creds);
    }

    @Override
    public void dismiss() {
      getWrappedAlert().dismiss();
    }

    @Override
    public String getText() {
      return getWrappedAlert().getText();
    }

    @Override
    public void sendKeys(String text) {
      getWrappedAlert().sendKeys(text);
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original alert. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original alert.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param alert                the underlying alert
     * @param wrapperClass         the class of a wrapper
     */
    public final static Alert wrapAlert(final WebDriverWrapper driverWrapper, final Alert alert, final Class<? extends AlertWrapper> wrapperClass) {
      AlertWrapper wrapper = null;
      Constructor<? extends AlertWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Alert.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Alert.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, alert);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapAlert();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original alert. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original alert.
     */
    public final Alert wrapAlert() {
      final Alert alert = getWrappedAlert();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(alert, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(alert);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Alert) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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

  public static class OptionsWrapper implements Options {

    private final Options originalOptions;
    private final WebDriverWrapper driverWrapper;

    public OptionsWrapper(final WebDriverWrapper driverWrapper, final Options options) {
      originalOptions = options;
      this.driverWrapper = driverWrapper;
    }

    public final Options getWrappedOptions() {
      return originalOptions;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public void addCookie(Cookie cookie) {
      getWrappedOptions().addCookie(cookie);
    }

    @Override
    public void deleteCookieNamed(String name) {
      getWrappedOptions().deleteCookieNamed(name);
    }

    @Override
    public void deleteCookie(Cookie cookie) {
      getWrappedOptions().deleteCookie(cookie);
    }

    @Override
    public void deleteAllCookies() {
      getWrappedOptions().deleteAllCookies();
    }

    @Override
    public Set<Cookie> getCookies() {
      return getWrappedOptions().getCookies();
    }

    @Override
    public Cookie getCookieNamed(String name) {
      return getWrappedOptions().getCookieNamed(name);
    }

    @Override
    public Timeouts timeouts() {
      return getDriverWrapper().wrapTimeouts(getWrappedOptions().timeouts());
    }

    @Override
    public ImeHandler ime() {
      return getWrappedOptions().ime();
    }

    @Override
    public Window window() {
      return getDriverWrapper().wrapWindow(getWrappedOptions().window());
    }

    @Override
    public Logs logs() {
      return getWrappedOptions().logs();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original options. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original options.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param options              the underlying options
     * @param wrapperClass         the class of a wrapper
     */
    public final static Options wrapOptions(final WebDriverWrapper driverWrapper, final Options options, final Class<? extends OptionsWrapper> wrapperClass) {
      OptionsWrapper wrapper = null;
      Constructor<? extends OptionsWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Options.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Options.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, options);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapOptions();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original options. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original options.
     */
    public final Options wrapOptions() {
      final Options options = getWrappedOptions();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(options, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(options);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Options) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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

  public static class TimeoutsWrapper implements Timeouts {

    private final Timeouts originalTimeouts;
    private final WebDriverWrapper driverWrapper;

    public TimeoutsWrapper(final WebDriverWrapper driverWrapper, final Timeouts timeouts) {
      originalTimeouts = timeouts;
      this.driverWrapper = driverWrapper;
    }

    public final Timeouts getWrappedTimeouts() {
      return originalTimeouts;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public Timeouts implicitlyWait(long timeout, TimeUnit timeUnit) {
      getWrappedTimeouts().implicitlyWait(timeout, timeUnit);
      return this;
    }

    @Override
    public Timeouts setScriptTimeout(long timeout, TimeUnit timeUnit) {
      getWrappedTimeouts().setScriptTimeout(timeout, timeUnit);
      return this;
    }

    @Override
    public Timeouts pageLoadTimeout(long timeout, TimeUnit timeUnit) {
      getWrappedTimeouts().pageLoadTimeout(timeout, timeUnit);
      return this;
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original timeouts. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original timeouts.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param timeouts             the underlying timeouts
     * @param wrapperClass         the class of a wrapper
     */
    public final static Timeouts wrapTimeouts(final WebDriverWrapper driverWrapper, final Timeouts timeouts, final Class<? extends TimeoutsWrapper> wrapperClass) {
      TimeoutsWrapper wrapper = null;
      Constructor<? extends TimeoutsWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Timeouts.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Timeouts.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, timeouts);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapTimeouts();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original timeouts. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original timeouts.
     */
    public final Timeouts wrapTimeouts() {
      final Timeouts timeouts = getWrappedTimeouts();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(timeouts, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(timeouts);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Timeouts) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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

  public static class WindowWrapper implements Window {

    private final Window originalWindow;
    private final WebDriverWrapper driverWrapper;

    public WindowWrapper(final WebDriverWrapper driverWrapper, final Window window) {
      originalWindow = window;
      this.driverWrapper = driverWrapper;
    }

    public final Window getWrappedWindow() {
      return originalWindow;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public void setSize(Dimension size) {
      getWrappedWindow().setSize(size);
    }

    @Override
    public void setPosition(Point position) {
      getWrappedWindow().setPosition(position);
    }

    @Override
    public Dimension getSize() {
      return getWrappedWindow().getSize();
    }

    @Override
    public Point getPosition() {
      return getWrappedWindow().getPosition();
    }

    @Override
    public void maximize() {
      getWrappedWindow().maximize();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original window. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original window.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param window               the underlying window
     * @param wrapperClass         the class of a wrapper
     */
    public final static Window wrapWindow(final WebDriverWrapper driverWrapper, final Window window, final Class<? extends WindowWrapper> wrapperClass) {
      WindowWrapper wrapper = null;
      Constructor<? extends WindowWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Window.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Window.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, window);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapWindow();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original window. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original window.
     */
    public final Window wrapWindow() {
      final Window window = getWrappedWindow();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(window, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(window);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Window) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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

  public static class CoordinatesWrapper implements Coordinates {

    private final Coordinates originalCoordinates;
    private final WebDriverWrapper driverWrapper;

    public CoordinatesWrapper(final WebDriverWrapper driverWrapper, final Coordinates coordinates) {
      originalCoordinates = coordinates;
      this.driverWrapper = driverWrapper;
    }

    public final Coordinates getWrappedCoordinates() {
      return originalCoordinates;
    }

    private WebDriverWrapper getDriverWrapper() {
      return driverWrapper;
    }

    @Override
    public Point onScreen() {
      return getWrappedCoordinates().onScreen();
    }

    @Override
    public Point inViewPort() {
      return getWrappedCoordinates().inViewPort();
    }

    @Override
    public Point onPage() {
      return getWrappedCoordinates().onPage();
    }

    @Override
    public Object getAuxiliary() {
      return getDriverWrapper().wrapObject(getWrappedCoordinates().getAuxiliary());
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original coordinates. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original coordinates.
     *
     * @param driverWrapper        the underlying driver's wrapper
     * @param coordinates          the underlying coordinates
     * @param wrapperClass         the class of a wrapper
     */
    public final static Coordinates wrapCoordinates(final WebDriverWrapper driverWrapper, final Coordinates coordinates, final Class<? extends CoordinatesWrapper> wrapperClass) {
      CoordinatesWrapper wrapper = null;
      Constructor<? extends CoordinatesWrapper> constructor = null;
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = wrapperClass.getConstructor(wrapperClass.getEnclosingClass(), Coordinates.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        try {
          constructor = wrapperClass.getConstructor(WebDriverWrapper.class, Coordinates.class);
        } catch (Exception e) {
        }
      }
      if (constructor == null) {
        throw new Error("Element wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, coordinates);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
      return wrapper.wrapCoordinates();
    }

    /**
     * Builds a {@link Proxy} implementing all interfaces of original coordinates. It will delegate calls to
     * wrapper when wrapper implements the requested method otherwise to original coordinates.
     */
    public final Coordinates wrapCoordinates() {
      final Coordinates coordinates = getWrappedCoordinates();
      final Set<Class<?>> wrapperInterfaces = extractInterfaces(this);

      final InvocationHandler handler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          try {
            if (wrapperInterfaces.contains(method.getDeclaringClass())) {
              beforeMethod(method, args);
              Object result = callMethod(method, args);
              afterMethod(method, result, args);
              return result;
            }
            return method.invoke(coordinates, args);
          } catch (InvocationTargetException e) {
            onError(method, e, args);
            throw e.getTargetException();
          }
        }
      };

      Set<Class<?>> allInterfaces = extractInterfaces(coordinates);
      allInterfaces.addAll(wrapperInterfaces);
      Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

      return (Coordinates) Proxy.newProxyInstance(
          this.getClass().getClassLoader(),
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