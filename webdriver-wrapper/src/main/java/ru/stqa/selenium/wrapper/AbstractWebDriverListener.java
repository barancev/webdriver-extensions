/*
 * Copyright 2013 Alexei Barantsev
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

public abstract class AbstractWebDriverListener implements WebDriverListener {

  public void beforeGet(WebDriver driver, String url) {}
  public void afterGet(WebDriver driver, String url) {}

  public void beforeGetCurrentUrl(WebDriver driver) {}
  public void afterGetCurrentUrl(WebDriver driver, String result) {}

  public void beforeGetTitle(WebDriver driver) {}
  public void afterGetTitle(WebDriver driver, String result) {}

  public void beforeFindElement(WebDriver driver, By locator) {}
  public void afterFindElement(WebDriver driver, WebElement result, By locator) {}

  public void beforeFindElements(WebDriver driver, By locator) {}
  public void afterFindElements(WebDriver driver, List<WebElement> result, By locator) {}

  public void beforeGetPageSource(WebDriver driver) {}
  public void afterGetPageSource(WebDriver driver, String result) {}

  public void beforeClose(WebDriver driver) {}
  public void afterClose(WebDriver driver) {}

  public void beforeQuit(WebDriver driver) {}
  public void afterQuit(WebDriver driver) {}

  public void beforeGetWindowHandles(WebDriver driver) {}
  public void afterGetWindowHandles(WebDriver driver, Set<String> result) {}

  public void beforeGetWindowHandle(WebDriver driver) {}
  public void afterGetWindowHandle(WebDriver driver, String result) {}

  public void beforeExecuteScript(WebDriver driver, String script, Object... args) {}
  public void afterExecuteScript(WebDriver driver, Object result, String script, Object... args) {}

  public void beforeExecuteAsyncScript(WebDriver driver, String script, Object... args) {}
  public void afterExecuteAsyncScript(WebDriver driver, Object result, String script, Object... args) {}

  public void beforeClick(WebElement element) {}
  public void afterClick(WebElement element) {}

  public void beforeSubmit(WebElement element) {}
  public void afterSubmit(WebElement element) {}

  public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {}
  public void afterSendKeys(WebElement element, CharSequence... keysToSend) {}

  public void beforeClear(WebElement element) {}
  public void afterClear(WebElement element) {}

  public void beforeGetTagName(WebElement element) {}
  public void afterGetTagName(WebElement element, String result) {}

  public void beforeGetAttribute(WebElement element, String name) {}
  public void afterGetAttribute(WebElement element, String result, String name) {}

  public void beforeIsSelected(WebElement element) {}
  public void afterIsSelected(WebElement element, boolean result) {}

  public void beforeIsEnabled(WebElement element) {}
  public void afterIsEnabled(WebElement element, boolean result) {}

  public void beforeGetText(WebElement element) {}
  public void afterGetText(WebElement element, String result) {}

  public void beforeFindElement(WebElement element, By locator) {}
  public void afterFindElement(WebElement element, WebElement result, By locator) {}

  public void beforeFindElements(WebElement element, By locator) {}
  public void afterFindElements(WebElement element, List<WebElement> result, By locator) {}

  public void beforeIsDisplayed(WebElement element) {}
  public void afterIsDisplayed(WebElement element, boolean result) {}

  public void beforeGetLocation(WebElement element) {}
  public void afterGetLocation(WebElement element, Point result) {}

  public void beforeGetSize(WebElement element) {}
  public void afterGetSize(WebElement element, Dimension result) {}

  public void beforeGetCssValue(WebElement element) {}
  public void afterGetCssValue(WebElement element, String result) {}

  public void beforeTo(WebDriver.Navigation navigation, String url) {}
  public void afterTo(WebDriver.Navigation navigation, String url) {}

  public void beforeTo(WebDriver.Navigation navigation, URL url) {}
  public void afterTo(WebDriver.Navigation navigation, URL url) {}

  public void beforeBack(WebDriver.Navigation navigation) {}
  public void afterBack(WebDriver.Navigation navigation) {}

  public void beforeForward(WebDriver.Navigation navigation) {}
  public void afterForward(WebDriver.Navigation navigation) {}

  public void beforeRefresh(WebDriver.Navigation navigation) {}
  public void afterRefresh(WebDriver.Navigation navigation) {}

  public void beforeAccept(Alert alert) {}
  public void afterAccept(Alert alert) {}

  public void beforeDismiss(Alert alert) {}
  public void afterDismiss(Alert alert) {}

  public void beforeGetText(Alert alert) {}
  public void afterGetText(Alert alert, String result) {}

  public void beforeSendKeys(Alert alert, String text) {}
  public void afterSendKeys(Alert alert, String text) {}

  public void beforeAddCookie(WebDriver.Options options, Cookie cookie) {}
  public void afterAddCookie(WebDriver.Options options, Cookie cookie) {}

  public void beforeDeleteCookieNamed(WebDriver.Options options, String name) {}
  public void afterDeleteCookieNamed(WebDriver.Options options, String name) {}

  public void beforeDeleteCookie(WebDriver.Options options, Cookie cookie) {}
  public void afterDeleteCookie(WebDriver.Options options, Cookie cookie) {}

  public void beforeDeleteAllCookies(WebDriver.Options options) {}
  public void afterDeleteAllCookies(WebDriver.Options options) {}

  public void beforeGetCookies(WebDriver.Options options) {}
  public void afterGetCookies(WebDriver.Options options, Set<Cookie> result) {}

  public void beforeGetCookieNamed(WebDriver.Options options, String name) {}
  public void afterGetCookieNamed(WebDriver.Options options, Cookie result, String name) {}

  public void beforeImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}
  public void afterImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}

  public void beforeSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}
  public void afterSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}

  public void beforePageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}
  public void afterPageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {}

  public void beforeGetSize(WebDriver.Window window) {}
  public void afterGetSize(WebDriver.Window window, Dimension result) {}

  public void beforeSetSize(WebDriver.Window window, Dimension size) {}
  public void afterSetSize(WebDriver.Window window, Dimension size) {}

  public void beforeGetPosition(WebDriver.Window window) {}
  public void afterGetPosition(WebDriver.Window window, Point result) {}

  public void beforeSetPosition(WebDriver.Window window, Point position) {}
  public void afterSetPosition(WebDriver.Window window, Point position) {}

  public void beforeMaximize(WebDriver.Window window) {}
  public void afterMaximize(WebDriver.Window window) {}

  public void beforeSendKeys(Keyboard keyboard, CharSequence... keysToSend) {}
  public void afterSendKeys(Keyboard keyboard, CharSequence... keysToSend) {}

  public void beforePressKey(Keyboard keyboard, CharSequence keysToSend) {}
  public void afterPressKey(Keyboard keyboard, CharSequence keysToSend) {}

  public void beforeReleaseKey(Keyboard keyboard, CharSequence keysToSend) {}
  public void afterReleaseKey(Keyboard keyboard, CharSequence keysToSend) {}

  public void beforeClick(Mouse mouse, Coordinates coordinates) {}
  public void afterClick(Mouse mouse, Coordinates coordinates) {}

  public void beforeDoubleClick(Mouse mouse, Coordinates coordinates) {}
  public void afterDoubleClick(Mouse mouse, Coordinates coordinates) {}

  public void beforeContextClick(Mouse mouse, Coordinates coordinates) {}
  public void afterContextClick(Mouse mouse, Coordinates coordinates) {}

  public void beforeMouseDown(Mouse mouse, Coordinates coordinates) {}
  public void afterMouseDown(Mouse mouse, Coordinates coordinates) {}

  public void beforeMouseUp(Mouse mouse, Coordinates coordinates) {}
  public void afterMouseUp(Mouse mouse, Coordinates coordinates) {}

  public void beforeMouseMove(Mouse mouse, Coordinates coordinates) {}
  public void afterMouseMove(Mouse mouse, Coordinates coordinates) {}

  public void beforeMouseMove(Mouse mouse, Coordinates coordinates, long x, long y) {}
  public void afterMouseMove(Mouse mouse, Coordinates coordinates, long x, long y) {}

  public void beforeSingleTap(TouchScreen touchScreen, Coordinates coordinates) {}
  public void afterSingleTap(TouchScreen touchScreen, Coordinates coordinates) {}

  public void beforeDoubleTap(TouchScreen touchScreen, Coordinates coordinates) {}
  public void afterDoubleTap(TouchScreen touchScreen, Coordinates coordinates) {}

  public void beforeLongPress(TouchScreen touchScreen, Coordinates coordinates) {}
  public void afterLongPress(TouchScreen touchScreen, Coordinates coordinates) {}

  public void beforeDown(TouchScreen touchScreen, long x, long y) {}
  public void afterDown(TouchScreen touchScreen, long x, long y) {}

  public void beforeUp(TouchScreen touchScreen, long x, long y) {}
  public void afterUp(TouchScreen touchScreen, long x, long y) {}

  public void beforeMove(TouchScreen touchScreen, long x, long y) {}
  public void afterMove(TouchScreen touchScreen, long x, long y) {}

  public void beforeScroll(TouchScreen touchScreen, long x, long y) {}
  public void afterScroll(TouchScreen touchScreen, long x, long y) {}

  public void beforeScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {}
  public void afterScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {}

  public void beforeFlick(TouchScreen touchScreen, long x, long y) {}
  public void afterFlick(TouchScreen touchScreen, long x, long y) {}

  public void beforeFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {}
  public void afterFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {}

}
