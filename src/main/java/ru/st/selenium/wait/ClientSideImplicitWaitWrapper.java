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

import com.google.common.base.Throwables;
import org.openqa.selenium.*;
import ru.st.selenium.wrapper.WebDriverWrapper;

import static ru.st.selenium.wait.RepeatableActions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientSideImplicitWaitWrapper extends WebDriverWrapper {

  public static ActionRepeater<WebDriver> with(WebDriver driver) {
    return new ActionRepeater<WebDriver>(driver);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds);
  }

  public static ActionRepeater<WebDriver> with(WebDriver driver, long timeOutInSeconds, long sleepInMillis) {
    return new ActionRepeater<WebDriver>(driver, timeOutInSeconds, sleepInMillis);
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

  @Override
  protected Class<? extends WebElementWrapper> getElementWrapperClass() {
    return ImplicitWaitElementWrapper.class;
  }

  @Override
  protected Class<? extends TargetLocatorWrapper> getTargetLocatorWrapperClass() {
    return ImplicitWaitTargetLocatorWrapper.class;
  }

  private static final long DEFAULT_TIMEOUT = 10;

  private long timeout = DEFAULT_TIMEOUT;
  private long interval = ActionRepeater.DEFAULT_SLEEP_TIMEOUT;

  public ClientSideImplicitWaitWrapper(final WebDriver driver) {
    this(driver, DEFAULT_TIMEOUT);
  }

  public ClientSideImplicitWaitWrapper(final WebDriver driver, long timeoutInSeconds) {
    this(driver, timeoutInSeconds, ActionRepeater.DEFAULT_SLEEP_TIMEOUT);
  }

  public ClientSideImplicitWaitWrapper(final WebDriver driver, long timeoutInSeconds, long sleepTimeOut) {
    super(driver);
    this.timeout = timeoutInSeconds;
    this.interval = sleepTimeOut;
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
  }

  protected ActionRepeater<WebDriver> withWebDriver() {
    return with(getWrappedDriver(), timeout, interval);
  }

  @Override
  public WebElement findElement(By locator) {
    try {
      return wrapElement(withWebDriver().tryTo(performFindElement(locator)));
    } catch (TimeoutException te) {
      throw Throwables.propagate(te.getCause());
    }
  }

  @Override
  public List<WebElement> findElements(By locator) {
    try {
      return wrapElements(withWebDriver().tryTo(performFindElements(locator)));
    } catch (TimeoutException te) {
      return new ArrayList<WebElement>();
    }
  }

  public class ImplicitWaitElementWrapper extends WebElementWrapper {

    public ImplicitWaitElementWrapper(WebElement element) {
      super(ClientSideImplicitWaitWrapper.this, element);
    }

    protected ActionRepeater<WebElement> withWebElement() {
      return with(getWrappedElement(), timeout, interval);
    }

    @Override
    public void click() {
      try {
        withWebElement().tryTo(performClick());
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public void submit() {
      try {
        withWebElement().tryTo(performSubmit());
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
      try {
        withWebElement().tryTo(performSendKeys(keysToSend));
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public void clear() {
      try {
        withWebElement().tryTo(performClear());
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public boolean isSelected() {
      try {
        return withWebElement().tryTo(checkIsSelected());
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public boolean isEnabled() {
      try {
        return withWebElement().tryTo(checkIsEnabled());
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public WebElement findElement(By locator) {
      try {
        return withWebElement().tryTo(performFindElement(locator));
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public List<WebElement> findElements(By locator) {
      try {
        return withWebElement().tryTo(performFindElements(locator));
      } catch (TimeoutException te) {
        return new ArrayList<WebElement>();
      }
    }
  }

  public class ImplicitWaitTargetLocatorWrapper extends TargetLocatorWrapper {

    public ImplicitWaitTargetLocatorWrapper(TargetLocator targetLocator) {
      super(ClientSideImplicitWaitWrapper.this, targetLocator);
    }

    @Override
    public Alert alert() {
      ActionRepeater<TargetLocator> repeater = new ActionRepeater<TargetLocator>(getWrappedTargetLocator(), timeout, interval);
      return repeater.tryTo(new AbstractRepeatableAction<TargetLocator, Alert>() {
          @Override
          public Alert apply(TargetLocator target) {
            return target.alert();
          }
          @Override
          public boolean shouldIgnoreException(Throwable t) {
            return t instanceof NoAlertPresentException;
          }
        }
      );
    }
  }
}