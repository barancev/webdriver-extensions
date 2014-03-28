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
package ru.stqa.selenium.wait;

import com.google.common.base.Throwables;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Each ActionRepeater instance defines the maximum amount of time to attempt to perform an action,
 * as well as the frequency with which to call the action.
 *
 * <p>
 * Sample usage: <code>
 *   // Try to look for an element to be present on the page, checking<br>
 *   // for its presence once every 5 seconds until timeout of 30 seconds is expired.<br>
 *   ActionRepeater&lt;WebDriver&gt; repeater = new ActionRepeater&lt;WebDriver&gt;(driver)<br>
 *       .withTimeout(30, SECONDS)<br>
 *       .pollingEvery(5, SECONDS);<br>
 *<br>
 *   WebElement foo = repeater.tryTo(new AbstractRepeatableAction&lt;SearchContext, WebElement&gt;() {<br>
 *     public WebElement apply(SearchContext context) {<br>
 *       return driver.findElement(By.id("foo"));<br>
 *     }<br>
 *     public boolean shouldIgnoreException(Throwable t) {<br>
 *       return t instanceof NoSuchElementException;<br>
 *     }<br>
 *   });
 * </code>
 *
 * <p>
 * <em>This class makes no thread safety guarantees.</em>
 *
 * @param <T> The action execution context type.
 */
public class ActionRepeater <T> {

  public static ActionRepeater<WebDriver> with(WebDriver driver) {
    return new ActionRepeater<WebDriver>(driver);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds, long sleepInMillis) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds, sleepInMillis);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepInMillis) {
    return new ActionRepeater<WebDriver>(driver, clock, sleeper, timeOutInSeconds, sleepInMillis);
  }

  public static ActionRepeater<WebElement> with(WebElement element) {
    return new ActionRepeater<WebElement>(element);
  }

  public static ActionRepeater<WebElement> with(WebElement element, long timeOutInSeconds) {
    return new ActionRepeater<WebElement>(element, timeOutInSeconds);
  }

  public static ActionRepeater<WebElement> with(WebElement element, long timeOutInSeconds, long sleepInMillis) {
    return new ActionRepeater<WebElement>(element, timeOutInSeconds, sleepInMillis);
  }

  public static ActionRepeater<WebElement> with(WebElement element, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepInMillis) {
    return new ActionRepeater<WebElement>(element, clock, sleeper, timeOutInSeconds, sleepInMillis);
  }

  public static Duration FIVE_HUNDRED_MILLIS = new Duration(500, MILLISECONDS);

  private final T context;
  private final Clock clock;
  private final Sleeper sleeper;

  private Duration timeout = FIVE_HUNDRED_MILLIS;
  private Duration interval = FIVE_HUNDRED_MILLIS;
  private String message = null;

  public final static long DEFAULT_SLEEP_TIMEOUT = 500;

  public ActionRepeater(T context) {
    this(context, DEFAULT_SLEEP_TIMEOUT);
  }

  public ActionRepeater(T context, long timeOutInSeconds) {
    this(context, timeOutInSeconds, DEFAULT_SLEEP_TIMEOUT);
  }

  public ActionRepeater(T context, long timeOutInSeconds, long sleepInMillis) {
    this(context, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, sleepInMillis);
  }

  /**
   * @param context The execution context to pass to the action
   * @param clock The clock to use when measuring the timeout
   * @param sleeper Object used to make the current thread go to sleep.
   * @param timeOutInSeconds The timeout in seconds when an expectation is
   * @param sleepTimeOut The timeout used whilst sleeping. Defaults to {@link #FIVE_HUNDRED_MILLIS}.
   */
  protected ActionRepeater(T context, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepTimeOut) {
    this.context = checkNotNull(context);
    this.clock = checkNotNull(clock);
    this.sleeper = checkNotNull(sleeper);
    withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
    pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
  }

  /**
   * Sets how long to wait for the action to return a result that should not be ignored.
   * The default timeout is {@link #FIVE_HUNDRED_MILLIS}.
   *
   * @param duration The timeout duration.
   * @param unit The unit of time.
   * @return A self reference.
   */
  public ActionRepeater<T> withTimeout(long duration, TimeUnit unit) {
    this.timeout = new Duration(duration, unit);
    return this;
  }

  /**
   * Sets how often the action should be repeated.
   *
   * <p>
   * In reality, the interval may be greater as the cost of actually evaluating the action
   * is not factored in. The default polling interval is {@link #FIVE_HUNDRED_MILLIS}.
   *
   * @param duration The timeout duration.
   * @param unit The unit of time.
   * @return A self reference.
   */
  public ActionRepeater<T> pollingEvery(long duration, TimeUnit unit) {
    this.interval = new Duration(duration, unit);
    return this;
  }

  /**
   * Repeatedly attempts to perform the action until one of the following occurs:
   * <ol>
   * <li>the function returns a value that should not be ignored,</li>
   * <li>the function throws a exception that should not be ignored,</li>
   * <li>the timeout expires,
   * <li>
   * <li>the current thread is interrupted</li>
   * </ol>
   *
   * @param action the action to be performed repeatedly
   * @param <V> The action's expected return type.
   * @return The action' return value if the action returned a result that should not be ignored.
   * @throws org.openqa.selenium.TimeoutException If the timeout expires.
   */
  public <V> V tryTo(RepeatableAction<? super T, V> action) {
    long end = clock.laterBy(timeout.in(MILLISECONDS));
    Throwable lastException = null;
    while (true) {
      try {
        V result = action.apply(context);
        if (! action.shouldIgnoreResult(result)) {
          return result;
        }
      } catch (Throwable e) {
        if (! action.shouldIgnoreException(e)) {
          throw Throwables.propagate(e);
        } else {
          lastException = e;
        }
      }

      // Check the timeout after evaluating the function to ensure conditions
      // with a zero timeout can succeed.
      if (!clock.isNowBefore(end)) {
        String toAppend = message == null ?
            " trying to perform action " + action.toString() : ": " + message;

        String timeoutMessage = String.format("Timed out after %d seconds%s",
            timeout.in(SECONDS), toAppend);
        throw new TimeoutException(timeoutMessage, lastException);
      }

      try {
        sleeper.sleep(interval);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new WebDriverException(e);
      }
    }
  }
}
