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

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface WebDriverListener {

  void beforeGet(WebDriver driver, String url);
  void afterGet(WebDriver driver, String url);

  void beforeGetCurrentUrl(WebDriver driver);
  void afterGetCurrentUrl(WebDriver driver, String result);

  void beforeGetTitle(WebDriver driver);
  void afterGetTitle(WebDriver driver, String result);

  void beforeFindElement(WebDriver driver, By locator);
  void afterFindElement(WebDriver driver, WebElement result, By locator);

  void beforeFindElements(WebDriver driver, By locator);
  void afterFindElements(WebDriver driver, List<WebElement> result, By locator);

  void beforeGetPageSource(WebDriver driver);
  void afterGetPageSource(WebDriver driver, String result);

  void beforeClose(WebDriver driver);
  void afterClose(WebDriver driver);

  void beforeQuit(WebDriver driver);
  void afterQuit(WebDriver driver);

  void beforeGetWindowHandles(WebDriver driver);
  void afterGetWindowHandles(WebDriver driver, Set<String> result);

  void beforeGetWindowHandle(WebDriver driver);
  void afterGetWindowHandle(WebDriver driver, String result);

  void beforeExecuteScript(WebDriver driver, String script, Object... args);
  void afterExecuteScript(WebDriver driver, Object result, String script, Object... args);

  void beforeExecuteAsyncScript(WebDriver driver, String script, Object... args);
  void afterExecuteAsyncScript(WebDriver driver, Object result, String script, Object... args);

  void beforeClick(WebElement element);
  void afterClick(WebElement element);

  void beforeSubmit(WebElement element);
  void afterSubmit(WebElement element);

  void beforeSendKeys(WebElement element, CharSequence... keysToSend);
  void afterSendKeys(WebElement element, CharSequence... keysToSend);

  void beforeClear(WebElement element);
  void afterClear(WebElement element);

  void beforeGetTagName(WebElement element);
  void afterGetTagName(WebElement element, String result);

  void beforeGetAttribute(WebElement element, String name);
  void afterGetAttribute(WebElement element, String result, String name);

  void beforeIsSelected(WebElement element);
  void afterIsSelected(WebElement element, boolean result);

  void beforeIsEnabled(WebElement element);
  void afterIsEnabled(WebElement element, boolean result);

  void beforeGetText(WebElement element);
  void afterGetText(WebElement element, String result);

  void beforeFindElement(WebElement element, By locator);
  void afterFindElement(WebElement element, WebElement result, By locator);

  void beforeFindElements(WebElement element, By locator);
  void afterFindElements(WebElement element, List<WebElement> result, By locator);

  void beforeIsDisplayed(WebElement element);
  void afterIsDisplayed(WebElement element, boolean result);

  void beforeGetLocation(WebElement element);
  void afterGetLocation(WebElement element, Point result);

  void beforeGetSize(WebElement element);
  void afterGetSize(WebElement element, Dimension result);

  void beforeGetCssValue(WebElement element);
  void afterGetCssValue(WebElement element, String result);

  void beforeTo(WebDriver.Navigation navigation, String url);
  void afterTo(WebDriver.Navigation navigation, String url);

  void beforeTo(WebDriver.Navigation navigation, URL url);
  void afterTo(WebDriver.Navigation navigation, URL url);

  void beforeBack(WebDriver.Navigation navigation);
  void afterBack(WebDriver.Navigation navigation);

  void beforeForward(WebDriver.Navigation navigation);
  void afterForward(WebDriver.Navigation navigation);

  void beforeRefresh(WebDriver.Navigation navigation);
  void afterRefresh(WebDriver.Navigation navigation);

  void beforeAccept(Alert alert);
  void afterAccept(Alert alert);

  void beforeDismiss(Alert alert);
  void afterDismiss(Alert alert);

  void beforeGetText(Alert alert);
  void afterGetText(Alert alert, String result);

  void beforeSendKeys(Alert alert, String text);
  void afterSendKeys(Alert alert, String text);

  void beforeAddCookie(WebDriver.Options options, Cookie cookie);
  void afterAddCookie(WebDriver.Options options, Cookie cookie);

  void beforeDeleteCookieNamed(WebDriver.Options options, String name);
  void afterDeleteCookieNamed(WebDriver.Options options, String name);

  void beforeDeleteCookie(WebDriver.Options options, Cookie cookie);
  void afterDeleteCookie(WebDriver.Options options, Cookie cookie);

  void beforeDeleteAllCookies(WebDriver.Options options);
  void afterDeleteAllCookies(WebDriver.Options options);

  void beforeGetCookies(WebDriver.Options options);
  void afterGetCookies(WebDriver.Options options, Set<Cookie> result);

  void beforeGetCookieNamed(WebDriver.Options options, String name);
  void afterGetCookieNamed(WebDriver.Options options, Cookie result, String name);

  void beforeImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);
  void afterImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);

  void beforeSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);
  void afterSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);

  void beforePageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);
  void afterPageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit);

  void beforeGetSize(WebDriver.Window window);
  void afterGetSize(WebDriver.Window window, Dimension result);

  void beforeSetSize(WebDriver.Window window, Dimension size);
  void afterSetSize(WebDriver.Window window, Dimension size);

  void beforeGetPosition(WebDriver.Window window);
  void afterGetPosition(WebDriver.Window window, Point result);

  void beforeSetPosition(WebDriver.Window window, Point position);
  void afterSetPosition(WebDriver.Window window, Point position);

  void beforeMaximize(WebDriver.Window window);
  void afterMaximize(WebDriver.Window window);

  void beforeSendKeys(Keyboard keyboard, CharSequence... keysToSend);
  void afterSendKeys(Keyboard keyboard, CharSequence... keysToSend);

  void beforePressKey(Keyboard keyboard, CharSequence keysToSend);
  void afterPressKey(Keyboard keyboard, CharSequence keysToSend);

  void beforeReleaseKey(Keyboard keyboard, CharSequence keysToSend);
  void afterReleaseKey(Keyboard keyboard, CharSequence keysToSend);

  void beforeClick(Mouse mouse, Coordinates coordinates);
  void afterClick(Mouse mouse, Coordinates coordinates);

  void beforeDoubleClick(Mouse mouse, Coordinates coordinates);
  void afterDoubleClick(Mouse mouse, Coordinates coordinates);

  void beforeContextClick(Mouse mouse, Coordinates coordinates);
  void afterContextClick(Mouse mouse, Coordinates coordinates);

  void beforeMouseDown(Mouse mouse, Coordinates coordinates);
  void afterMouseDown(Mouse mouse, Coordinates coordinates);

  void beforeMouseUp(Mouse mouse, Coordinates coordinates);
  void afterMouseUp(Mouse mouse, Coordinates coordinates);

  void beforeMouseMove(Mouse mouse, Coordinates coordinates);
  void afterMouseMove(Mouse mouse, Coordinates coordinates);

  void beforeMouseMove(Mouse mouse, Coordinates coordinates, long x, long y);
  void afterMouseMove(Mouse mouse, Coordinates coordinates, long x, long y);

  void beforeSingleTap(TouchScreen touchScreen, Coordinates coordinates);
  void afterSingleTap(TouchScreen touchScreen, Coordinates coordinates);

  void beforeDoubleTap(TouchScreen touchScreen, Coordinates coordinates);
  void afterDoubleTap(TouchScreen touchScreen, Coordinates coordinates);

  void beforeLongPress(TouchScreen touchScreen, Coordinates coordinates);
  void afterLongPress(TouchScreen touchScreen, Coordinates coordinates);

  void beforeDown(TouchScreen touchScreen, long x, long y);
  void afterDown(TouchScreen touchScreen, long x, long y);

  void beforeUp(TouchScreen touchScreen, long x, long y);
  void afterUp(TouchScreen touchScreen, long x, long y);

  void beforeMove(TouchScreen touchScreen, long x, long y);
  void afterMove(TouchScreen touchScreen, long x, long y);

  void beforeScroll(TouchScreen touchScreen, long x, long y);
  void afterScroll(TouchScreen touchScreen, long x, long y);

  void beforeScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y);
  void afterScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y);

  void beforeFlick(TouchScreen touchScreen, long x, long y);
  void afterFlick(TouchScreen touchScreen, long x, long y);

  void beforeFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y);
  void afterFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y);

}
