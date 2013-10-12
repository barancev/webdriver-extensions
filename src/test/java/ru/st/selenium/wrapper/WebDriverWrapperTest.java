/*
 * Copyright 2013 Software Freedom Conservancy.
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
package ru.st.selenium.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ru.st.selenium.StubDriver;

public class WebDriverWrapperTest {

  @Test
  public void shouldNotAddInterfacesNotAvailableInTheOriginalDriver() {
    final WebDriver driver = new StubDriver();
    assertFalse(driver instanceof JavascriptExecutor);

    final WebDriver wrapper = WebDriverWrapper.buildWrapper(driver, SimpleWebDriverWrapper.class);
    assertFalse(wrapper instanceof JavascriptExecutor);
  }

  @Test
  public void shouldRespectInterfacesAvailableInTheOriginalDriver() {
    final WebDriver driver = mock(ExtendedDriver.class);
    assertTrue(driver instanceof SomeOtherInterface);

    final WebDriver wrapper = WebDriverWrapper.buildWrapper(driver, SimpleWebDriverWrapper.class);
    assertTrue(wrapper instanceof SomeOtherInterface);
  }

  @Test
  public void click() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class, "elt2");

    when(mockedDriver.findElement(By.name("foo"))).thenReturn(mockedElement);
    when(mockedElement.findElement(By.tagName("DIV"))).thenReturn(mockedElement2);
    when(mockedElement2.isDisplayed()).thenReturn(true);

    final AtomicInteger counter = new AtomicInteger();
    final WebDriver testedDriver = ClickCountingDriver.decorate(mockedDriver, counter);

    // check for element wrapped by the driver
    final WebElement firstElement = testedDriver.findElement(By.name("foo"));
    firstElement.click();
    assertEquals(1, counter.get());

    // check that elements returned by WebElement.findElement are wrapped too 
    final WebElement nestedElement = firstElement.findElement(By.tagName("DIV"));
    nestedElement.click();
    assertEquals(2, counter.get());

    // check that methods that are not overwritten works normally 
    assertTrue(nestedElement.isDisplayed());

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(1)).click();
    verify(mockedElement, times(1)).findElement(By.tagName("DIV"));
    verify(mockedElement2, times(1)).click();
    verify(mockedElement2, times(1)).isDisplayed();
  }

  private static class SimpleWebDriverWrapper extends WebDriverWrapper {
    public SimpleWebDriverWrapper(final WebDriver driver) {
      super(driver);
    }
  }

  private static interface SomeOtherInterface {}

  private static interface ExtendedDriver extends WebDriver, SomeOtherInterface {}

  private static class ClickCountingDriver extends WebDriverWrapper {
    private final AtomicInteger counter;

    private ClickCountingDriver(final WebDriver driver, final AtomicInteger clickCounter) {
      super(driver);
      counter = clickCounter;
    }

    public static WebDriver decorate(final WebDriver driver, final AtomicInteger clickCounter) {
      final ClickCountingDriver wrapper = new ClickCountingDriver(driver, clickCounter);
      return WebDriverWrapper.buildWrapper(driver, wrapper);
    }

    @Override
    protected WebElement wrapElement(WebElement element) {
      return new WebElementWrapper(this, element) {
        @Override
        public void click() {
          super.click();
          counter.incrementAndGet();
        }
      };
    }
  }

}
