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

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.server.DefaultDriverProvider;
import org.openqa.selenium.remote.server.DriverProvider;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebDriverFactoryInternalTest {

  private WebDriverFactoryInternal factory;

  @Before
  public void setUp() {
    factory = new SingletonStorage();
  }

  @Test
  public void testCanInstantiateAndDismissAStandardDriver() {
    WebDriver driver = factory.getDriver(DesiredCapabilities.htmlUnit());
    assertThat(driver, instanceOf(HtmlUnitDriver.class));
    assertFalse(factory.isEmpty());

    factory.dismissDriver(driver);
    assertTrue(factory.isEmpty());
  }

  @Test
  public void testCanInstantiateAndDismissADriverByClassName() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());

    factory.addDriverProvider(new DefaultDriverProvider(
        capabilities, FakeWebDriver.class.getName()));

    WebDriver driver = factory.getDriver(capabilities);
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(factory.isEmpty());

    factory.dismissDriver(driver);
    assertTrue(factory.isEmpty());
  }

  @Test
  public void throwsOnAttemptToInstantiateADriverByBadClassName() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("FAKE");

    try {
      WebDriver driver = factory.getDriver(capabilities);
      fail("Exception expected");
    } catch (Exception expected) {
    }

    assertTrue(factory.isEmpty());
  }

  @Test
  public void testCanInstantiateAndDismissADriverWithACustomDriverProvider() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("FAKE");
    factory.addDriverProvider(
        new DefaultDriverProvider(capabilities, FakeWebDriver.class.getName()));

    WebDriver driver = factory.getDriver(capabilities);
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(factory.isEmpty());

    factory.dismissDriver(driver);
    assertTrue(factory.isEmpty());
  }

  @Test
  public void testCanOverrideExistingDriverProvider() {
    factory.addDriverProvider(
        new DefaultDriverProvider(DesiredCapabilities.firefox(),
            FakeWebDriver.class.getName()));

    WebDriver driver = factory.getDriver(DesiredCapabilities.firefox());
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(factory.isEmpty());

    factory.dismissDriver(driver);
    assertTrue(factory.isEmpty());
  }

  @Test
  public void testCanInstantiateARemoteDriver() {
    factory.addRemoteDriverProvider(new RemoteDriverProvider() {
      @Override
      public WebDriver createDriver(String hub, Capabilities capabilities) {
        return new FakeWebDriver(capabilities);
      }
    });

    factory.setDefaultHub("some url");

    WebDriver driver = factory.getDriver(DesiredCapabilities.firefox());
    assertThat(driver, instanceOf(FakeWebDriver.class));
    assertFalse(factory.isEmpty());

    factory.dismissDriver(driver);
    assertTrue(factory.isEmpty());
  }

  @Test
  public void testCanNotInstantiateARemoteDriverWithBadUrl() {
    factory.setDefaultHub("some url");

    try {
      WebDriver driver = factory.getDriver(DesiredCapabilities.firefox());
      fail("Exception expected");
    } catch (Error ignored) {
    }

    assertTrue(factory.isEmpty());
  }

}
