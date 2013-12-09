package ru.st.selenium.wait;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;

import java.util.concurrent.TimeUnit;

public class ActionRepeater <T> extends FluentWait<T> {

  public final static long DEFAULT_SLEEP_TIMEOUT = 500;

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
  protected ActionRepeater(T context, Clock clock, Sleeper sleeper, long timeOutInSeconds,
                           long sleepTimeOut) {
    super(context, clock, sleeper);
    withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
    pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
  }

  public <V> V tryTo(RepeatableAction<? super T, V> action) {
    return until(action);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds);
  }

  public static ActionRepeater<WebElement> with(WebElement element, long timeOutInSeconds) {
    return new ActionRepeater<WebElement>(element, timeOutInSeconds);
  }

}
