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

import org.openqa.selenium.*;

import java.util.List;

public class RepeatableActions {

  private RepeatableActions() {
    // Utility class
  }

  public static RepeatableAction<SearchContext, WebElement> performFindElement(final By locator) {
    return new AbstractRepeatableAction<SearchContext, WebElement>() {
      @Override
      public WebElement apply(SearchContext context) {
        return context.findElement(locator);
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof NoSuchElementException;
      }
    };
  }

  public static RepeatableAction<SearchContext, List<WebElement>> performFindElements(final By locator) {
    return new AbstractRepeatableAction<SearchContext, List<WebElement>>() {
      @Override
      public List<WebElement> apply(SearchContext context) {
        return context.findElements(locator);
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof NoSuchElementException;
      }

      @Override
      public boolean shouldIgnoreResult(List<WebElement> result) {
        return result.isEmpty();
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> performClick() {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        element.click();
        return true;
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> performSubmit() {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        element.submit();
        return true;
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> performSendKeys(final CharSequence... keysToSend) {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        element.sendKeys(keysToSend);
        return true;
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> performClear() {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        element.clear();
        return true;
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> checkIsSelected() {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        return element.isSelected();
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> checkIsEnabled() {
    return new AbstractRepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        return element.isEnabled();
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof ElementNotVisibleException;
      }
    };
  }

  public static RepeatableAction<WebDriver, Alert> performSwitchToAlert() {
    return new AbstractRepeatableAction<WebDriver, Alert>() {
      @Override
      public Alert apply(WebDriver driver) {
        return driver.switchTo().alert();
      }
      @Override
      public boolean shouldIgnoreException(Throwable t) {
        return t instanceof NoAlertPresentException;
      }
    };
  }

}
