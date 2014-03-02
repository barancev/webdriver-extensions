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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class ThreadLocalSingletonModeTest {

  DesiredCapabilities capabilities;

  @Before
  public void initFactoryAndCapabilities() {
    WebDriverFactory.dismissAll();
    WebDriverFactory.setMode(WebDriverFactoryMode.THREADLOCAL_SINGLETON);

    capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(FakeWebDriver.class.getName());
  }

  private boolean isActive(WebDriver driver) {
    return ((FakeWebDriver) driver).isActive();
  }

  @Test
  public void testCanInstantiateAndDismissADriver() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));
    assertFalse(WebDriverFactory.isEmpty());

    WebDriverFactory.dismissDriver(driver);
    assertFalse(isActive(driver));
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test
  public void testCanDismissAllDrivers() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));
    assertFalse(WebDriverFactory.isEmpty());

    WebDriverFactory.dismissAll();
    assertFalse(isActive(driver));
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test
  public void testShouldReuseADriverWithSameCapabilities() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));

    WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
    assertSame(driver2, driver);
    assertTrue(isActive(driver));
  }

  @Test
  public void testShouldRecreateADriverWithDifferentCapabilities() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));

    capabilities.setCapability("foo", "bar");
    WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
    assertNotSame(driver2, driver);
    assertTrue(isActive(driver2));
    assertFalse(isActive(driver));
  }

  @Test
  public void testShouldRecreateAnInactiveDriver() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));
    driver.quit();

    WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
    assertNotSame(driver2, driver);
    assertTrue(isActive(driver2));
    assertFalse(isActive(driver));
  }

  @Test(expected = Error.class)
  public void testShouldDismissOwnedDriversOnly() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    assertTrue(isActive(driver));

    WebDriver driver2 = new FakeWebDriver(capabilities);
    assertNotSame(driver2, driver);

    WebDriverFactory.dismissDriver(driver2);
  }

  @Test
  public void testShouldCreateADriverForEachThread() throws InterruptedException {
    final WebDriver driver = WebDriverFactory.getDriver(capabilities);

    Thread t = new Thread() {
      @Override
      public void run() {
        WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
        assertNotSame(driver2, driver);
      }
    };
    t.start();
    t.join();
  }

  @Test
  public void testShouldNotDismissADriverFromAnotherThread() throws InterruptedException {
    final WebDriver driver = WebDriverFactory.getDriver(capabilities);
    final List<Throwable> thrown = new ArrayList<Throwable>();

    Thread t = new Thread() {
      @Override
      public void run() {
        WebDriverFactory.dismissDriver(driver);
      }
    };
    t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        thrown.add(e);
      }
    });
    t.start();

    t.join();

    assertTrue(isActive(driver));
    assertFalse(WebDriverFactory.isEmpty());
    assertFalse(thrown.isEmpty());
  }

  @Test
  public void testDismissAllCanDismissDriversFromAllThreads() {
    final WebDriver driver = WebDriverFactory.getDriver(capabilities);

    new Thread() {
      @Override
      public void run() {
        WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
        assertNotSame(driver2, driver);
      }
    }.start();

    WebDriverFactory.dismissAll();
    assertFalse(isActive(driver));
    assertTrue(WebDriverFactory.isEmpty());
  }

  @Test
  public void testShouldRecreateADriverAfterDismissAll() {
    WebDriver driver = WebDriverFactory.getDriver(capabilities);
    WebDriverFactory.dismissAll();

    WebDriver driver2 = WebDriverFactory.getDriver(capabilities);
    assertNotSame(driver2, driver);
    assertTrue(isActive(driver2));
    assertFalse(isActive(driver));
  }

}
