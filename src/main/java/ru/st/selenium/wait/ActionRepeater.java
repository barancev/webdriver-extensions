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
package ru.st.selenium.wait;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;

import java.util.concurrent.TimeUnit;

public class ActionRepeater <T> extends FluentWait<T> {

  public final static long DEFAULT_SLEEP_TIMEOUT = 500;

  public static ActionRepeater<WebDriver> with(WebDriver driver) {
    return new ActionRepeater<WebDriver>(driver);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds);
  }

  public static ActionRepeater<WebElement> with(WebElement element) {
    return new ActionRepeater<WebElement>(element);
  }

  public static ActionRepeater<WebElement> with(WebElement element, long timeOutInSeconds) {
    return new ActionRepeater<WebElement>(element, timeOutInSeconds);
  }

  public ActionRepeater(T context) {
    this(context, new SystemClock(), Sleeper.SYSTEM_SLEEPER, DEFAULT_SLEEP_TIMEOUT, DEFAULT_SLEEP_TIMEOUT);
  }

  public ActionRepeater(T context, long timeOutInSeconds) {
    this(context, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, DEFAULT_SLEEP_TIMEOUT);
  }

  public ActionRepeater(T context, long timeOutInSeconds, long sleepInMillis) {
    this(context, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, sleepInMillis);
  }

  /**
   * @param context The WebDriver instance to pass to the expected conditions
   * @param clock The clock to use when measuring the timeout
   * @param sleeper Object used to make the current thread go to sleep.
   * @param timeOutInSeconds The timeout in seconds when an expectation is
   * @param sleepTimeOut The timeout used whilst sleeping. Defaults to 500ms called.
   */
  protected ActionRepeater(T context, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepTimeOut) {
    super(context, clock, sleeper);
    withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
    pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
  }

  @Override
  public void until(Predicate<T> isTrue) {
    throw new UnsupportedOperationException("Use method tryTo instead");
  }

  @Override
  public <V> V until(Function<? super T, V> isTrue) {
    throw new UnsupportedOperationException("Use method tryTo instead");
  }

  public <V> V tryTo(RepeatableAction<? super T, V> action) {
    cleanIgnoreList();
    ignoreAll(action.ignoredExceptions());
    return super.until(action);
  }

}
