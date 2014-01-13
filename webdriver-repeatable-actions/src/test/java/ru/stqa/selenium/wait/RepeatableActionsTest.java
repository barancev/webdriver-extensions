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

import com.google.common.collect.Lists;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static ru.stqa.selenium.wait.RepeatableActions.*;

public class RepeatableActionsTest {

  @Test
  public void findElementActionShouldCallFindElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    WebElement element = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement));
  }

  @Test
  public void findElementActionShouldIgnoreNoSuchElementException() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    WebElement element = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement));
  }

  @Test
  public void findElementsActionShouldCallFindElements() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedDriver, times(1)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void findElementsActionShouldIgnoreEmptyList() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void findElementsActionShouldIgnoreNoSuchElementException() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElements(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void findElementInAnotherElementActionShouldCallFindElement() {
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);

    when(mockedElement1.findElement(By.name("foo")))
        .thenReturn(mockedElement2);

    WebElement element = new ActionRepeater<WebElement>(mockedElement1, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedElement1, times(1)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement2));
  }

  @Test
  public void findElementInAnotherElementActionShouldIgnoreNoSuchElementException() {
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);

    when(mockedElement1.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement2);

    WebElement element = new ActionRepeater<WebElement>(mockedElement1, 1, 1)
        .tryTo(performFindElement(By.name("foo")));

    verify(mockedElement1, times(3)).findElement(By.name("foo"));
    assertThat(element, is(mockedElement2));
  }

  @Test
  public void findElementsInAnotherElementActionShouldCallFindElements() {
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedElement.findElements(By.name("foo")))
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedElement, times(1)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void findElementsInAnotherElementActionShouldIgnoreEmptyList() {
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedElement.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedElement, times(3)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void findElementsInAnotherElementActionShouldIgnoreNoSuchElementException() {
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedElement.findElements(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElements);

    List<WebElement> elements = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performFindElements(By.name("foo")));

    verify(mockedElement, times(3)).findElements(By.name("foo"));
    assertThat(elements, is(mockedElements));
  }

  @Test
  public void clickActionShouldCallClick() {
    final WebElement mockedElement = mock(WebElement.class);

    doNothing().when(mockedElement).click();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performClick());

    verify(mockedElement, times(1)).click();
  }

  @Test
  public void clickActionShouldIgnoreElementNotVisibleException() {
    final WebElement mockedElement = mock(WebElement.class);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).click();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performClick());

    verify(mockedElement, times(3)).click();
  }

  @Test
  public void submitActionShouldCallSubmit() {
    final WebElement mockedElement = mock(WebElement.class);

    doNothing().when(mockedElement).submit();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performSubmit());

    verify(mockedElement, times(1)).submit();
  }

  @Test
  public void submitActionShouldIgnoreElementNotVisibleException() {
    final WebElement mockedElement = mock(WebElement.class);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).submit();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performSubmit());

    verify(mockedElement, times(3)).submit();
  }

  @Test
  public void sendKeysActionShouldCallSendKeys() {
    final String text = "To be or not to be";
    final WebElement mockedElement = mock(WebElement.class);

    doNothing().when(mockedElement).sendKeys(text);

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performSendKeys(text));

    verify(mockedElement, times(1)).sendKeys(text);
  }

  @Test
  public void sendKeysActionShouldIgnoreElementNotVisibleException() {
    final String text = "To be or not to be";
    final WebElement mockedElement = mock(WebElement.class);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).sendKeys(text);

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performSendKeys(text));

    verify(mockedElement, times(3)).sendKeys(text);
  }

  @Test
  public void clearActionShouldCallClear() {
    final WebElement mockedElement = mock(WebElement.class);

    doNothing().when(mockedElement).clear();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performClear());

    verify(mockedElement, times(1)).clear();
  }

  @Test
  public void clearActionShouldIgnoreElementNotVisibleException() {
    final WebElement mockedElement = mock(WebElement.class);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).clear();

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(performClear());

    verify(mockedElement, times(3)).clear();
  }

  @Test
  public void isSelectedCheckShouldCallIsSelected() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isSelected()).thenReturn(true);

    boolean res = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsSelected());

    assertThat(res, is(true));
    verify(mockedElement, times(1)).isSelected();
  }

  @Test
  public void isSelectedCheckShouldNotWaitForTheElementToBeSelected() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isSelected()).thenReturn(false);

    boolean res = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsSelected());

    assertThat(res, is(false));
    verify(mockedElement, times(1)).isSelected();
  }

  @Test
  public void isSelectedCheckShouldIgnoreElementNotVisibleException() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsSelected());

    verify(mockedElement, times(3)).isSelected();
  }

  @Test
  public void isEnabledCheckShouldCallIsSelected() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isEnabled()).thenReturn(true);

    boolean res = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsEnabled());

    assertThat(res, is(true));
    verify(mockedElement, times(1)).isEnabled();
  }

  @Test
  public void isEnabledCheckShouldNotWaitForTheElementToBeEnabled() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isEnabled()).thenReturn(false);

    boolean res = new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsEnabled());

    assertThat(res, is(false));
    verify(mockedElement, times(1)).isEnabled();
  }

  @Test
  public void isEnabledCheckShouldIgnoreElementNotVisibleException() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    new ActionRepeater<WebElement>(mockedElement, 1, 1)
        .tryTo(checkIsEnabled());

    verify(mockedElement, times(3)).isEnabled();
  }

  @Test
  public void switchToAlertActionShouldCallSwitchToAlert() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert()).thenReturn(mockedAlert);

    Alert alert = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performSwitchToAlert());

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(1)).alert();
    assertThat(alert, is(mockedAlert));
  }

  @Test
  public void switchToAlertActionShouldIgnoreNoAlertPresentException() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert())
        .thenThrow(NoAlertPresentException.class)
        .thenThrow(NoAlertPresentException.class)
        .thenReturn(mockedAlert);

    Alert alert = new ActionRepeater<WebDriver>(mockedDriver, 1, 1)
        .tryTo(performSwitchToAlert());

    verify(mockedDriver, times(3)).switchTo();
    verify(mockedSwitch, times(3)).alert();
    assertThat(alert, is(mockedAlert));
  }

}
