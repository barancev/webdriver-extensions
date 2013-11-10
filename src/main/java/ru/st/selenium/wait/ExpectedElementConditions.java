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

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class ExpectedElementConditions {

  private ExpectedElementConditions() {
    // Utility class
  }

  public static ExpectedElementCondition<Boolean> isPresent() {
    return new ExpectedElementCondition<Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        try {
          element.getTagName();
          return true;
        } catch (StaleElementReferenceException e) {
          return false;
        }
      }
    };
  }

  public static ExpectedElementCondition<Boolean> isDisplayed() {
    return new ExpectedElementCondition<Boolean>() {
      @Override
      public Boolean apply(WebElement element) {
        return element.isDisplayed();
      }
    };
  }

}
