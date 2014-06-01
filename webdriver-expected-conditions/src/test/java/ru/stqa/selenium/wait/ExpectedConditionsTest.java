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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ru.stqa.selenium.wait.ExpectedConditions.*;
import static ru.stqa.selenium.wait.ExpectedElementConditions.*;
import static ru.stqa.selenium.wait.ExpectedListConditions.*;

public class ExpectedConditionsTest {

  @Test
  public void testFirstElementLocatedIsPresent() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final List<WebElement> list = new ArrayList<WebElement>() {{
      add(mock(WebElement.class));
      add(mock(WebElement.class));
    }};
    when(mockedDriver.findElements(By.id("my-id")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(list);

    TestingClock clock = new TestingClock();
    WebDriverWait wait = new Waiter(mockedDriver, clock, clock, 1, 100);

    WebElement element = wait.until(firstElementLocated(By.id("my-id"), isPresent()));

    assertEquals(element, list.get(0));
    assertEquals(clock.now(), 100L);
    verify(mockedDriver, times(2)).findElements(By.id("my-id"));
    verifyZeroInteractions(list.get(0));
    verifyZeroInteractions(list.get(1));
  }

  @Test
  public void testFirstElementLocatedIsDisplayed() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final List<WebElement> list = new ArrayList<WebElement>() {{
      add(mock(WebElement.class));
      add(mock(WebElement.class));
    }};
    when(mockedDriver.findElements(By.id("my-id")))
        .thenReturn(list);
    when(list.get(0).isDisplayed())
        .thenReturn(false)
        .thenReturn(true);

    TestingClock clock = new TestingClock();
    WebDriverWait wait = new Waiter(mockedDriver, clock, clock, 1, 100);

    WebElement element = wait.until(firstElementLocated(By.id("my-id"), isVisible()));

    assertEquals(element, list.get(0));
    assertEquals(clock.now(), 100L);
    verify(mockedDriver, times(2)).findElements(By.id("my-id"));
    verify(list.get(0), times(2)).isDisplayed();
    verifyZeroInteractions(list.get(1));
  }

  @Test
  public void testSomeElementLocatedIsDisplayed() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final List<WebElement> list = new ArrayList<WebElement>() {{
      add(mock(WebElement.class));
      add(mock(WebElement.class));
    }};
    when(mockedDriver.findElements(By.id("my-id")))
        .thenReturn(list);
    when(list.get(0).isDisplayed())
        .thenReturn(false)
        .thenReturn(false)
        .thenReturn(true);
    when(list.get(1).isDisplayed())
        .thenReturn(false)
        .thenReturn(true)
        .thenReturn(true);

    TestingClock clock = new TestingClock();
    WebDriverWait wait = new Waiter(mockedDriver, clock, clock, 1, 100);

    WebElement element = wait.until(someElementLocated(By.id("my-id"), isVisible()));

    assertEquals(element, list.get(1));
    assertEquals(clock.now(), 100L);
    verify(mockedDriver, times(2)).findElements(By.id("my-id"));
    verify(list.get(0), times(2)).isDisplayed();
    verify(list.get(1), times(2)).isDisplayed();
  }

  @Test
  public void testEachElementLocatedIsDisplayed() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final List<WebElement> list = new ArrayList<WebElement>() {{
      add(mock(WebElement.class));
      add(mock(WebElement.class));
    }};
    when(mockedDriver.findElements(By.id("my-id")))
        .thenReturn(list);
    when(list.get(0).isDisplayed())
        .thenReturn(false)
        .thenReturn(true);
    when(list.get(1).isDisplayed())
        .thenReturn(false)
        .thenReturn(true);

    TestingClock clock = new TestingClock();
    WebDriverWait wait = new Waiter(mockedDriver, clock, clock, 1, 100);

    List<WebElement> elements = wait.until(eachElementLocated(By.id("my-id"), isVisible()));

    assertEquals(elements, list);
    assertEquals(clock.now(), 200L);
    verify(mockedDriver, times(3)).findElements(By.id("my-id"));
    verify(list.get(0), times(3)).isDisplayed();
    verify(list.get(1), times(2)).isDisplayed();
  }

  @Test
  public void testListOfElementsLocatedIsNotEmpty() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final List<WebElement> list = new ArrayList<WebElement>() {{
      add(mock(WebElement.class));
      add(mock(WebElement.class));
    }};
    when(mockedDriver.findElements(By.id("my-id")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(list);

    TestingClock clock = new TestingClock();
    WebDriverWait wait = new Waiter(mockedDriver, clock, clock, 1, 100);

    List<WebElement> elements = wait.until(listOfElementsLocated(By.id("my-id"), isNotEmpty()));

    assertEquals(elements, list);
    assertEquals(clock.now(), 100L);
    verify(mockedDriver, times(2)).findElements(By.id("my-id"));
    verifyZeroInteractions(list.get(0));
    verifyZeroInteractions(list.get(1));
  }
}
