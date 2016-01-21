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
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import ru.stqa.selenium.wrapper.WebDriverWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.stqa.selenium.wait.RepeatableActions.*;

public class ClientSideImplicitWaitWrapper extends WebDriverWrapper {

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
  private Clock clock;
  private Sleeper sleeper;

  public ClientSideImplicitWaitWrapper(final WebDriver driver) {
    this(driver, DEFAULT_TIMEOUT);
  }

  public ClientSideImplicitWaitWrapper(final WebDriver driver, long timeoutInSeconds) {
    this(driver, timeoutInSeconds, ActionRepeater.DEFAULT_SLEEP_TIMEOUT);
  }

  public ClientSideImplicitWaitWrapper(final WebDriver driver, long timeoutInSeconds, long sleepTimeOut) {
    this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeoutInSeconds, sleepTimeOut);
  }

  protected ClientSideImplicitWaitWrapper(final WebDriver driver, Clock clock, Sleeper sleeper, long timeoutInSeconds, long sleepTimeOut) {
    super(driver);
    this.timeout = timeoutInSeconds;
    this.interval = sleepTimeOut;
    this.clock = clock;
    this.sleeper = sleeper;
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
  }

  protected ActionRepeater<WebDriver> withWebDriver() {
    return ActionRepeater.with(getWrappedDriver(), clock, sleeper, timeout, interval);
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
      return ActionRepeater.with(getWrappedElement(), clock, sleeper, timeout, interval);
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
        return wrapElement(withWebElement().tryTo(performFindElement(locator)));
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public List<WebElement> findElements(By locator) {
      try {
        return wrapElements(withWebElement().tryTo(performFindElements(locator)));
      } catch (TimeoutException te) {
        return new ArrayList<WebElement>();
      }
    }

    @Override
    public Coordinates getCoordinates() {
      try {
        return withWebElement().tryTo(new AbstractRepeatableAction<WebElement, Coordinates>() {
          @Override
          public Coordinates apply(WebElement target) {
            return wrapCoordinates(((Locatable) target).getCoordinates());
          }

          @Override
          public boolean shouldIgnoreException(Throwable t) {
            return t instanceof ElementNotVisibleException;
          }
        }
        );
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }
  }

  public class ImplicitWaitTargetLocatorWrapper extends TargetLocatorWrapper {

    public ImplicitWaitTargetLocatorWrapper(TargetLocator targetLocator) {
      super(ClientSideImplicitWaitWrapper.this, targetLocator);
    }

    @Override
    public Alert alert() {
      ActionRepeater<TargetLocator> repeater = new ActionRepeater<TargetLocator>(getWrappedTargetLocator(), clock, sleeper, timeout, interval);
      try {
        return repeater.tryTo(new AbstractRepeatableAction<TargetLocator, Alert>() {
            @Override
            public Alert apply(TargetLocator target) {
              return wrapAlert(target.alert());
            }

            @Override
            public boolean shouldIgnoreException(Throwable t) {
              return t instanceof NoAlertPresentException;
            }
          }
        );
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public WebDriver frame(final int index) {
      ActionRepeater<TargetLocator> repeater = new ActionRepeater<TargetLocator>(getWrappedTargetLocator(), clock, sleeper, timeout, interval);
      try {
        return repeater.tryTo(new AbstractRepeatableAction<TargetLocator, WebDriver>() {
            @Override
            public WebDriver apply(TargetLocator target) {
              return target.frame(index);
            }

            @Override
            public boolean shouldIgnoreException(Throwable t) {
              return t instanceof NoSuchFrameException;
            }
          }
        );
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }

    @Override
    public WebDriver frame(final String idOrName) {
      ActionRepeater<TargetLocator> repeater = new ActionRepeater<TargetLocator>(getWrappedTargetLocator(), clock, sleeper, timeout, interval);
      try {
        return repeater.tryTo(new AbstractRepeatableAction<TargetLocator, WebDriver>() {
            @Override
            public WebDriver apply(TargetLocator target) {
              return target.frame(idOrName);
            }

            @Override
            public boolean shouldIgnoreException(Throwable t) {
              return t instanceof NoSuchFrameException;
            }
          }
        );
      } catch (TimeoutException te) {
        throw Throwables.propagate(te.getCause());
      }
    }
  }

}
