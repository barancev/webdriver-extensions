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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static ru.st.selenium.wait.ExpectedConditions.*;
import static ru.st.selenium.wait.ExpectedElementConditions.*;

public class ExpectedConditionsTest {

  public void sample() {
    WebDriver driver = null;
    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(firstElementLocated(By.id("my-id"), isPresent())).click();
  }
}
