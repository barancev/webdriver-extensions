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
import org.openqa.selenium.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static ru.stqa.selenium.wait.ActionRepeater.with;
import static ru.stqa.selenium.wait.RepeatableActions.performFindElement;

public class ActionRepeaterTest {

  @Test
  public void shouldReturnImmediatelyIfTheActionSucceeds() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    WebElement element = with(mockedDriver, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement));
  }

  @Test
  public void shouldReturnAsSoonAsTheActionSucceeds() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    WebElement element = with(mockedDriver, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement));
  }

  @Test
  public void shouldThrowIfTheActionNeverSucceeds() {
    final WebDriver mockedDriver = mock(WebDriver.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class);

    TestingClock clock = new TestingClock();

    try {
      with(mockedDriver, clock, clock, 1, 100)
          .tryTo(performFindElement(By.name("foo")));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(TimeoutException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(11)).findElement(By.name("foo"));
  }

  @Test
  public void shouldThrowExceptionsThatAreNotIgnored() {
    final WebDriver mockedDriver = mock(WebDriver.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchWindowException.class);

    try {
      with(mockedDriver, 1, 1)
          .tryTo(performFindElement(By.name("foo")));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchWindowException.class));
    }

    verify(mockedDriver, times(2)).findElement(By.name("foo"));
  }

}
