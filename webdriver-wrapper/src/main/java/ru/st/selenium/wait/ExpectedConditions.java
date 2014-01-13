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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.base.Function;

public class ExpectedConditions {

  private ExpectedConditions() {
    // Utility class
  }

  public static ExpectedCondition<WebElement> element(
      final WebElement element, final Function<WebElement, Boolean> condition) {
  return new ExpectedCondition<WebElement>() {
    @Override
    public WebElement apply(SearchContext context) {
      if (element != null && condition.apply(element)) {
        return element;
      } else {
        return null;
      }
    }
  };
}

  public static ExpectedCondition<WebElement> firstElementLocated(
        final By locator, final Function<WebElement, Boolean> condition) {
    return new ExpectedCondition<WebElement>() {
      @Override
      public WebElement apply(SearchContext context) {
        WebElement element = findElement(context, locator);
        if (element != null) {
          return condition.apply(element) ? element : null;
        } else {
          return null;
        }
      }
    };
  }

  public static ExpectedCondition<WebElement> anyElementLocated(
        final By locator, final Function<WebElement, Boolean> condition) {
    return new ExpectedCondition<WebElement>() {
      @Override
      public WebElement apply(SearchContext context) {
        List<WebElement> elements = context.findElements(locator);
        for (WebElement element : elements) {
          if (element != null && condition.apply(element)) {
            return element;
          }
        }
        return null;
      }
    };
  }

  public static ExpectedCondition<List<WebElement>> listOfElementsLocated(
      final By locator, final Function<List<WebElement>, Boolean> condition) {
    return new ExpectedCondition<List<WebElement>>() {
      @Override
      public List<WebElement> apply(SearchContext context) {
        List<WebElement> elements = context.findElements(locator);
        return condition.apply(elements) ? elements : null;
      }
    };
  }

  public static ExpectedCondition<List<WebElement>> eachElementLocated(
      final By locator, final Function<WebElement, Boolean> condition) {
    return new ExpectedCondition<List<WebElement>>() {
      @Override
      public List<WebElement> apply(SearchContext context) {
        List<WebElement> elements = context.findElements(locator);
        for (WebElement element : elements) {
          if (! condition.apply(element)) {
            return null;
          }
        }
        return elements;
      }
    };
  }

  private static WebElement findElement(SearchContext context, By locator) {
    List<WebElement> elements = context.findElements(locator);
    return elements.size() > 0 ? elements.get(0) : null;
  }
}
