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

import com.google.common.base.Throwables;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.security.Credentials;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This class allows to extend WebDriver by adding new functionality to a wrapper.
 * Example of use:
 * <code>WebDriver driver = WebDriverWrapper.wrapDriver(originalDriver, MyWebDriverWrapper.class);</code>
 * or
 * <code>MyWebDriverWrapper wrapper = new MyWebDriverWrapper(originalDriver, otherParameter);<br>
 * WebDriver driver = new MyWebDriverWrapper(originalDriver, otherParameter).getDriver();</code>
 */
public class WebDriverWrapper extends AbstractWrapper<WebDriver>
    implements WebDriver, WrapsDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen {

  private WebDriver enhancedDriver = null;

  public WebDriverWrapper(WebDriver driver) {
    super(null, driver);
  }

  protected Class<? extends WebElementWrapper> getElementWrapperClass() {
    return WebElementWrapper.class;
  }

  protected WebElement wrapElement(final WebElement element) {
    return WebElementWrapper.wrapOriginal(this, element, getElementWrapperClass());
  }

  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  protected Class<? extends TargetLocatorWrapper> getTargetLocatorWrapperClass() {
    return TargetLocatorWrapper.class;
  }

  protected TargetLocator wrapTargetLocator(final TargetLocator targetLocator) {
    return TargetLocatorWrapper.wrapOriginal(this, targetLocator, getTargetLocatorWrapperClass());
  }

  protected Class<? extends AlertWrapper> getAlertWrapperClass() {
    return AlertWrapper.class;
  }

  protected Alert wrapAlert(final Alert alert) {
    return AlertWrapper.wrapOriginal(this, alert, getAlertWrapperClass());
  }

  protected Class<? extends NavigationWrapper> getNavigationWrapperClass() {
    return NavigationWrapper.class;
  }

  protected Navigation wrapNavigation(final Navigation navigator) {
    return NavigationWrapper.wrapOriginal(this, navigator, getNavigationWrapperClass());
  }

  protected Class<? extends OptionsWrapper> getOptionsWrapperClass() {
    return OptionsWrapper.class;
  }

  protected Options wrapOptions(final Options options) {
    return OptionsWrapper.wrapOriginal(this, options, getOptionsWrapperClass());
  }

  protected Class<? extends TimeoutsWrapper> getTimeoutsWrapperClass() {
    return TimeoutsWrapper.class;
  }

  protected Timeouts wrapTimeouts(final Timeouts timeouts) {
    return TimeoutsWrapper.wrapOriginal(this, timeouts, getTimeoutsWrapperClass());
  }

  protected Class<? extends WindowWrapper> getWindowWrapperClass() {
    return WindowWrapper.class;
  }

  protected Window wrapWindow(final Window window) {
    return WindowWrapper.wrapOriginal(this, window, getWindowWrapperClass());
  }

  protected Class<? extends CoordinatesWrapper> getCoordinatesWrapperClass() {
    return CoordinatesWrapper.class;
  }

  protected Coordinates wrapCoordinates(final Coordinates coordinates) {
    return CoordinatesWrapper.wrapOriginal(this, coordinates, getCoordinatesWrapperClass());
  }

  protected Class<? extends KeyboardWrapper> getKeyboardWrapperClass() {
    return KeyboardWrapper.class;
  }

  protected Keyboard wrapKeyboard(final Keyboard keyboard) {
    return KeyboardWrapper.wrapOriginal(this, keyboard, getKeyboardWrapperClass());
  }

  protected Class<? extends MouseWrapper> getMouseWrapperClass() {
    return MouseWrapper.class;
  }

  protected Mouse wrapMouse(final Mouse mouse) {
    return MouseWrapper.wrapOriginal(this, mouse, getMouseWrapperClass());
  }

  protected Class<? extends TouchScreenWrapper> getTouchScreenWrapperClass() {
    return TouchScreenWrapper.class;
  }

  protected TouchScreen wrapTouchScreen(final TouchScreen touchScreen) {
    return TouchScreenWrapper.wrapOriginal(this, touchScreen, getTouchScreenWrapperClass());
  }

  // TODO: implement proper wrapping for arbitrary objects
  private Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return wrapElement((WebElement) object);
    } else {
      return object;
    }
  }

  protected void beforeMethodGlobal(AbstractWrapper target, Method method, Object[] args) {
  }

  protected Object callMethodGlobal(AbstractWrapper target, Method method, Object[] args) throws Throwable {
    return method.invoke(target, args);
  }

  protected void afterMethodGlobal(AbstractWrapper target, Method method, Object res, Object[] args) {
  }

  protected Object onErrorGlobal(AbstractWrapper target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    throw Throwables.propagate(e.getTargetException());
  }

  @Override
  public final WebDriver getWrappedDriver() {
    return getWrappedOriginal();
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

  @Override
  public Keyboard getKeyboard() {
    return wrapKeyboard(((HasInputDevices) getWrappedDriver()).getKeyboard());
  }

  @Override
  public Mouse getMouse() {
    return wrapMouse(((HasInputDevices) getWrappedDriver()).getMouse());
  }

  @Override
  public TouchScreen getTouch() {
    return wrapTouchScreen(((HasTouchScreen) getWrappedDriver()).getTouch());
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   *
   * @param driver               the underlying driver
   * @param wrapperClass         the class of a wrapper
   * @return                     a proxy that wraps the original driver
   */
  public static WebDriver wrapDriver(final WebDriver driver, final Class<? extends WebDriverWrapper> wrapperClass) {
    return wrapOriginal(null, driver, wrapperClass);
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   */
  public WebDriver getDriver() {
    if (enhancedDriver == null) {
      enhancedDriver = wrapOriginal();
    }
    return enhancedDriver;
  }

  /**
   * Simple {@link WrapsElement} delegating all calls to the wrapped {@link WebElement}.
   * The methods {@link WebDriverWrapper#wrapElement(WebElement)}/{@link WebDriverWrapper#wrapElements(List)} will
   * be called on the related {@link WebDriverWrapper} to wrap the elements returned by {@link #findElement(By)}/{@link #findElements(By)}.
   */
  public static class WebElementWrapper extends AbstractWrapper<WebElement> implements WebElement, WrapsElement, Locatable {

    public WebElementWrapper(final WebDriverWrapper driverWrapper, final WebElement element) {
      super(driverWrapper, element);
    }

    @Override
    public final WebElement getWrappedElement() {
      return getWrappedOriginal();
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
    public Rectangle getRect() {
      return getWrappedElement().getRect();
    }

    @Override
    public String getCssValue(final String propertyName) {
      return getWrappedElement().getCssValue(propertyName);
    }

    public Coordinates getCoordinates() {
      Locatable locatable = (Locatable) getWrappedElement();
      return getDriverWrapper().wrapCoordinates(locatable.getCoordinates());
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
      return getWrappedElement().getScreenshotAs(outputType);
    }
  }

  public static class TargetLocatorWrapper extends AbstractWrapper<TargetLocator> implements TargetLocator {

    public TargetLocatorWrapper(final WebDriverWrapper driverWrapper, final TargetLocator targetLocator) {
      super(driverWrapper, targetLocator);
    }

    public final TargetLocator getWrappedTargetLocator() {
      return getWrappedOriginal();
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
    public WebDriver parentFrame() {
      getWrappedTargetLocator().parentFrame();
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
  }

  public static class NavigationWrapper extends AbstractWrapper<Navigation> implements Navigation {

    public NavigationWrapper(final WebDriverWrapper driverWrapper, final Navigation navigator) {
      super(driverWrapper, navigator);
    }

    public final Navigation getWrappedNavigation() {
      return getWrappedOriginal();
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
  }

  public static class AlertWrapper extends AbstractWrapper<Alert> implements Alert {

    public AlertWrapper(final WebDriverWrapper driverWrapper, final Alert alert) {
      super(driverWrapper, alert);
    }

    public final Alert getWrappedAlert() {
      return getWrappedOriginal();
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

    @Override
    public void setCredentials(Credentials credentials) {
      getWrappedAlert().setCredentials(credentials);
    }
  }

  public static class OptionsWrapper extends AbstractWrapper<Options> implements Options {

    public OptionsWrapper(final WebDriverWrapper driverWrapper, final Options options) {
      super(driverWrapper, options);
    }

    public final Options getWrappedOptions() {
      return getWrappedOriginal();
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
  }

  public static class TimeoutsWrapper extends AbstractWrapper<Timeouts> implements Timeouts {

    public TimeoutsWrapper(final WebDriverWrapper driverWrapper, final Timeouts timeouts) {
      super(driverWrapper, timeouts);
    }

    public final Timeouts getWrappedTimeouts() {
      return getWrappedOriginal();
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
  }

  public static class WindowWrapper extends AbstractWrapper<Window> implements Window {

    public WindowWrapper(final WebDriverWrapper driverWrapper, final Window window) {
      super(driverWrapper, window);
    }

    public final Window getWrappedWindow() {
      return getWrappedOriginal();
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

    @Override
    public void fullscreen() {
      getWrappedWindow().fullscreen();
    }
  }

  public static class CoordinatesWrapper extends AbstractWrapper<Coordinates> implements Coordinates {

    public CoordinatesWrapper(final WebDriverWrapper driverWrapper, final Coordinates coordinates) {
      super(driverWrapper, coordinates);
    }

    public final Coordinates getWrappedCoordinates() {
      return getWrappedOriginal();
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
  }

  public static class KeyboardWrapper extends AbstractWrapper<Keyboard> implements Keyboard {

    public KeyboardWrapper(final WebDriverWrapper driverWrapper, final Keyboard keyboard) {
      super(driverWrapper, keyboard);
    }

    public final Keyboard getWrappedKeyboard() {
      return getWrappedOriginal();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
      getWrappedKeyboard().sendKeys(charSequences);
    }

    @Override
    public void pressKey(CharSequence charSequence) {
      getWrappedKeyboard().pressKey(charSequence);
    }

    @Override
    public void releaseKey(CharSequence charSequence) {
      getWrappedKeyboard().releaseKey(charSequence);
    }
  }

  public static class MouseWrapper extends AbstractWrapper<Mouse> implements Mouse {

    public MouseWrapper(final WebDriverWrapper driverWrapper, final Mouse mouse) {
      super(driverWrapper, mouse);
    }

    public final Mouse getWrappedMouse() {
      return getWrappedOriginal();
    }

    @Override
    public void click(Coordinates coordinates) {
      getWrappedMouse().click(coordinates);
    }

    @Override
    public void doubleClick(Coordinates coordinates) {
      getWrappedMouse().doubleClick(coordinates);
    }

    @Override
    public void mouseDown(Coordinates coordinates) {
      getWrappedMouse().mouseDown(coordinates);
    }

    @Override
    public void mouseUp(Coordinates coordinates) {
      getWrappedMouse().mouseUp(coordinates);
    }

    @Override
    public void mouseMove(Coordinates coordinates) {
      getWrappedMouse().mouseMove(coordinates);
    }

    @Override
    public void mouseMove(Coordinates coordinates, long x, long y) {
      getWrappedMouse().mouseMove(coordinates, x, y);
    }

    @Override
    public void contextClick(Coordinates coordinates) {
      getWrappedMouse().contextClick(coordinates);
    }
  }

  public static class TouchScreenWrapper extends AbstractWrapper<TouchScreen> implements TouchScreen {

    public TouchScreenWrapper(final WebDriverWrapper driverWrapper, final TouchScreen touchScreen) {
      super(driverWrapper, touchScreen);
    }

    public final TouchScreen getWrappedTouchScreen() {
      return getWrappedOriginal();
    }

    @Override
    public void singleTap(Coordinates coordinates) {
      getWrappedTouchScreen().singleTap(coordinates);
    }

    @Override
    public void down(int x, int y) {
      getWrappedTouchScreen().down(x, y);
    }

    @Override
    public void up(int x, int y) {
      getWrappedTouchScreen().up(x, y);
    }

    @Override
    public void move(int x, int y) {
      getWrappedTouchScreen().move(x, y);
    }

    @Override
    public void scroll(Coordinates coordinates, int x, int y) {
      getWrappedTouchScreen().scroll(coordinates, x, y);
    }

    @Override
    public void doubleTap(Coordinates coordinates) {
      getWrappedTouchScreen().doubleTap(coordinates);
    }

    @Override
    public void longPress(Coordinates coordinates) {
      getWrappedTouchScreen().longPress(coordinates);
    }

    @Override
    public void scroll(int x, int y) {
      getWrappedTouchScreen().scroll(x, y);
    }

    @Override
    public void flick(int xSpeed, int ySpeed) {
      getWrappedTouchScreen().flick(xSpeed, ySpeed);
    }

    @Override
    public void flick(Coordinates coordinates, int x, int y, int speed) {
      getWrappedTouchScreen().flick(coordinates, x, y, speed);
    }
  }
}
