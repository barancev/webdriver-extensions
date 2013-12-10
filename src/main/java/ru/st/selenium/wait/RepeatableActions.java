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
import com.google.common.collect.Lists;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepeatableActions {

  private RepeatableActions() {
    // Utility class
  }

  public static RepeatableAction<SearchContext, WebElement> performFindElement(final By locator) {
    return new RepeatableAction<SearchContext, WebElement>() {
      @Override
      public WebElement apply(SearchContext context) {
        return context.findElement(locator);
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(NoSuchElementException.class));
      }
    };
  }

  public static RepeatableAction<SearchContext, List<WebElement>> performFindElements(final By locator) {
    return new RepeatableAction<SearchContext, List<WebElement>>() {
      @Override
      public List<WebElement> apply(SearchContext context) {
        return context.findElements(locator);
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(NoSuchElementException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Void> performClick() {
    return new RepeatableAction<WebElement, Void>() {
      @Override
      public Void apply(WebElement element) {
        element.click();
        return null;
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Void> performSubmit() {
    return new RepeatableAction<WebElement, Void>() {
      @Override
      public Void apply(WebElement element) {
        element.submit();
        return null;
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Void> performSendKeys(final CharSequence... keysToSend) {
    return new RepeatableAction<WebElement, Void>() {
      @Override
      public Void apply(WebElement element) {
        element.sendKeys(keysToSend);
        return null;
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Void> performClear() {
    return new RepeatableAction<WebElement, Void>() {
      @Override
      public Void apply(WebElement element) {
        element.clear();
        return null;
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> checkIsSelected() {
    return new RepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        return element.isSelected();
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

  public static RepeatableAction<WebElement, Boolean> checkIsEnabled() {
    return new RepeatableAction<WebElement, Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        return element.isEnabled();
      }
      @Override
      public List<Class<? extends Throwable>> ignoredExceptions() {
        return new ArrayList<Class<? extends Throwable>>(Arrays.asList(ElementNotVisibleException.class));
      }
    };
  }

}
