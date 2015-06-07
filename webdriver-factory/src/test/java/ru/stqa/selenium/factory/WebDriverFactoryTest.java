/*
 * Copyright 2014 Alexei Barantsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.stqa.selenium.factory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.junit.Assert.*;

public class WebDriverFactoryTest {

  @Test
  public void testCanInstantiateAndDismissADriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());

    assertTrue(WebDriverFactory.isEmpty());

    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(WebDriverFactory.isEmpty());

    WebDriverFactory.dismissDriver(driver);
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test
  public void testCanDismissAllDrivers() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());

    assertTrue(WebDriverFactory.isEmpty());

    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(WebDriverFactory.isEmpty());

    WebDriverFactory.dismissAll();
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test
  public void testCanChangeModeIfEmpty() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());

    assertTrue(WebDriverFactory.isEmpty());

    WebDriverFactory.setMode(WebDriverFactoryMode.SINGLETON);
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test(expected = Error.class)
  public void testCannotChangeModeIfNonEmpty() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());

    assertTrue(WebDriverFactory.isEmpty());

    WebDriver driver = WebDriverFactory.getDriver(capabilities);

    try {
      WebDriverFactory.setMode(WebDriverFactoryMode.SINGLETON);
    } finally {
      WebDriverFactory.dismissDriver(driver);
      assertTrue(WebDriverFactory.isEmpty());
    }
  }

}
