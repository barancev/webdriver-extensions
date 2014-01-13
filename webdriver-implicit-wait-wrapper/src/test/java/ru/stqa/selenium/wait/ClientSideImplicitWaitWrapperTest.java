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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ClientSideImplicitWaitWrapperTest {

  private WebDriver getMockedDriver() {
    final WebDriver mockedDriver = mock(WebDriver.class,
        withSettings().extraInterfaces(HasInputDevices.class));
    final WebDriver.Options mockedOptions = mock(WebDriver.Options.class);
    final WebDriver.Timeouts mockedTimeouts = mock(WebDriver.Timeouts.class);
    when(mockedDriver.manage()).thenReturn(mockedOptions);
    when(mockedOptions.timeouts()).thenReturn(mockedTimeouts);
    when(mockedTimeouts.implicitlyWait(0, TimeUnit.SECONDS)).thenReturn(null);
    return mockedDriver;
  }

  @Test
  public void findElementShouldImplicitlyWaitForAnElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebElement element = driver.findElement(By.name("foo"));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    assertThat(mockedElement, equalTo(element));
  }

  @Test
  public void findElementShouldThrowIfElementIsNotFound() {
    final WebDriver mockedDriver = getMockedDriver();

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    verify(mockedDriver, times(11)).findElement(By.name("foo"));
  }

  @Test
  public void findElementsShouldImplicitlyWaitForAtLeastOneElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(mockedElements);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    List<WebElement> elements = driver.findElements(By.name("foo"));

    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    assertThat(mockedElements, equalTo(elements));
  }

  @Test
  public void findElementsShouldReturnEmptyListIfNoElementIsFound() {
    final WebDriver mockedDriver = getMockedDriver();

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>());

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    List<WebElement> elements = driver.findElements(By.name("foo"));

    verify(mockedDriver, times(11)).findElements(By.name("foo"));
    assertThat(elements.size(), is(0));
  }

  @Test
  public void clickShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

   doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).click();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    driver.findElement(By.name("foo")).click();

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).click();
  }

  @Test
  public void clickShouldThrowIfTheElementIsNotVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).click();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).click();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).click();
  }

  @Test
  public void submitShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).submit();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    driver.findElement(By.name("foo")).submit();

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).submit();
  }

  @Test
  public void submitShouldThrowIfTheElementIsNotVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).submit();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).submit();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).submit();
  }

  @Test
  public void sendKeysShouldImplicitlyWaitForTheElementToBeVisible() {
    final String text = "To be or not to be";
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).sendKeys(text);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    driver.findElement(By.name("foo")).sendKeys(text);

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).sendKeys(text);
  }

  @Test
  public void sendKeysShouldThrowIfTheElementIsNotVisible() {
    final String text = "To be or not to be";
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).sendKeys(text);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).sendKeys(text);
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).sendKeys(text);
  }

  @Test
  public void clearShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).clear();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    driver.findElement(By.name("foo")).clear();

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).clear();
  }

  @Test
  public void clearShouldThrowIfTheElementIsNotVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).clear();

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).clear();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).clear();
  }

  @Test
  public void isSelectedShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    boolean selected = driver.findElement(By.name("foo")).isSelected();

    assertThat(selected, is(true));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isSelected();
  }

  @Test
  public void isSelectedShouldImplicitlyWaitForTheElementToBeVisibleEvenIfTheElementIsNotSelected() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(false);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    boolean selected = driver.findElement(By.name("foo")).isSelected();

    assertThat(selected, is(false));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isSelected();
  }

  @Test
  public void isSelectedShouldThrowIfTheElementIsNotVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).isSelected();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).isSelected();
  }

  @Test
  public void isEnabledShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    boolean selected = driver.findElement(By.name("foo")).isEnabled();

    assertThat(selected, is(true));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isEnabled();
  }

  @Test
  public void isEnabledShouldImplicitlyWaitForTheElementToBeVisibleEvenIfTheElementIsNotEnabled() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(false);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    boolean selected = driver.findElement(By.name("foo")).isEnabled();

    assertThat(selected, is(false));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isEnabled();
  }

  @Test
  public void isEnabledShouldThrowIfTheElementIsNotVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.findElement(By.name("foo")).isEnabled();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).isEnabled();
  }

  @Test
  public void findElementInAnotherElementShouldImplicitlyWaitForAnElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElement(By.name("bar")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement2);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebElement element = driver.findElement(By.name("foo"));
    WebElement element2 = element.findElement(By.name("bar"));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).findElement(By.name("bar"));
    assertThat(mockedElement2, equalTo(element2));
  }

  @Test
  public void findElementInAnotherElementShouldThrowIfElementIsNotFound() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElement(By.name("bar")))
        .thenThrow(NoSuchElementException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    WebElement element = driver.findElement(By.name("foo"));

    try {
      element.findElement(By.name("bar"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).findElement(By.name("bar"));
  }

  @Test
  public void findElementsInAnotherElementShouldImplicitlyWaitForAtLeastOneElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElements(By.name("bar")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(mockedElements);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebElement element = driver.findElement(By.name("foo"));
    List<WebElement> elements = element.findElements(By.name("bar"));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).findElements(By.name("bar"));
    assertThat(mockedElements, equalTo(elements));
  }

  @Test
  public void findElementsInAnotherElementShouldReturnEmptyListIfNoElementIsFound() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>());

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    WebElement element = driver.findElement(By.name("foo"));
    List<WebElement> elements = element.findElements(By.name("foo"));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).findElements(By.name("foo"));
    assertThat(elements.size(), is(0));
  }

  @Test
  public void findElementChainShouldImplicitlyWaitForAnElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final WebElement mockedElement3 = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement1);

    when(mockedElement1.findElement(By.name("bar")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement2);

    when(mockedElement2.findElement(By.name("baz")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement3);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebElement element1 = driver.findElement(By.name("foo"));
    WebElement element2 = element1.findElement(By.name("bar"));
    WebElement element3 = element2.findElement(By.name("baz"));

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement1, times(3)).findElement(By.name("bar"));
    verify(mockedElement2, times(3)).findElement(By.name("baz"));
    assertThat(mockedElement3, equalTo(element3));
  }

  @Test
  public void findElementChainShouldThrowIfElementIsNotFound() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement1);

    when(mockedElement1.findElement(By.name("bar")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement2);

    when(mockedElement2.findElement(By.name("baz")))
        .thenThrow(NoSuchElementException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    WebElement element1 = driver.findElement(By.name("foo"));
    WebElement element2 = element1.findElement(By.name("bar"));

    try {
      element2.findElement(By.name("baz"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement1, times(3)).findElement(By.name("bar"));
    verify(mockedElement2, times(11)).findElement(By.name("baz"));
  }

  @Test
  public void findElementsChainShouldImplicitlyWaitForAnElementToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final WebElement mockedElement3 = mock(WebElement.class);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(Lists.newArrayList(mockedElement1));

    when(mockedElement1.findElements(By.name("bar")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(Lists.newArrayList(mockedElement2));

    when(mockedElement2.findElements(By.name("baz")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(Lists.newArrayList(mockedElement3));

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    List<WebElement> elements1 = driver.findElements(By.name("foo"));
    List<WebElement> elements2 = elements1.get(0).findElements(By.name("bar"));
    List<WebElement> elements3 = elements2.get(0).findElements(By.name("baz"));

    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    verify(mockedElement1, times(3)).findElements(By.name("bar"));
    verify(mockedElement2, times(3)).findElements(By.name("baz"));
    assertThat(mockedElement3, equalTo(elements3.get(0)));
  }

  @Test
  public void findElementsChainShouldThrowIfElementIsNotFound() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(Lists.newArrayList(mockedElement1));

    when(mockedElement1.findElements(By.name("bar")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(Lists.newArrayList(mockedElement2));

    when(mockedElement2.findElements(By.name("baz")))
        .thenReturn(new ArrayList<WebElement>());

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    List<WebElement> elements1 = driver.findElements(By.name("foo"));
    List<WebElement> elements2 = elements1.get(0).findElements(By.name("bar"));
    List<WebElement> elements3 = elements2.get(0).findElements(By.name("baz"));

    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    verify(mockedElement1, times(3)).findElements(By.name("bar"));
    verify(mockedElement2, times(11)).findElements(By.name("baz"));
    assertThat(elements3.size(), is(0));
  }

  @Test
  public void switchToAlertShouldImplicitlyWaitForAnAlertToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert())
        .thenThrow(NoAlertPresentException.class)
        .thenThrow(NoAlertPresentException.class)
        .thenReturn(mockedAlert);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    Alert alert = driver.switchTo().alert();

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).alert();
    assertThat(mockedAlert, is(alert));
  }

  @Test
  public void switchToAlertShouldThrowIfThereIsNoAlert() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert())
        .thenThrow(NoAlertPresentException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.switchTo().alert();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoAlertPresentException.class));
    }

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).alert();
  }

  @Test
  public void switchToFrameByIndexShouldImplicitlyWaitForAFrameToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame(1))
        .thenThrow(NoSuchFrameException.class)
        .thenThrow(NoSuchFrameException.class)
        .thenReturn(mockedDriver);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebDriver newDriver = driver.switchTo().frame(1);

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).frame(1);
    assertThat(newDriver, is(driver));
  }

  @Test
  public void switchToFrameByIndexShouldThrowIfThereIsNoFrame() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame(1))
        .thenThrow(NoSuchFrameException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.switchTo().frame(1);
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchFrameException.class));
    }

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).frame(1);
  }

  @Test
  public void switchToFrameByNameShouldImplicitlyWaitForAFrameToBePresent() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame("myname"))
        .thenThrow(NoSuchFrameException.class)
        .thenThrow(NoSuchFrameException.class)
        .thenReturn(mockedDriver);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 1).getDriver();

    WebDriver newDriver = driver.switchTo().frame("myname");

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).frame("myname");
    assertThat(newDriver, is(driver));
  }

  @Test
  public void switchToFrameByNameShouldThrowIfThereIsNoFrame() {
    final WebDriver mockedDriver = getMockedDriver();
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame("myname"))
        .thenThrow(NoSuchFrameException.class);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();

    try {
      driver.switchTo().frame("myname");
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchFrameException.class));
    }

    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).frame("myname");
  }

  private interface LocatableElement extends WebElement, Locatable {}

  @Test
  public void interactionsClickShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebDriver mockedDriver = getMockedDriver();
    final Keyboard mockedKeyboard = mock(Keyboard.class);
    final Mouse mockedMouse = mock(Mouse.class);

    final LocatableElement mockedElement = mock(LocatableElement.class);
    final Coordinates mockedCoords = mock(Coordinates.class);

    when(((HasInputDevices) mockedDriver).getKeyboard())
        .thenReturn(mockedKeyboard);
    when(((HasInputDevices) mockedDriver).getMouse())
        .thenReturn(mockedMouse);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.getCoordinates())
        .thenThrow(new ElementNotVisibleException("1"))
        .thenThrow(new ElementNotVisibleException("2"))
        .thenReturn(mockedCoords);

    WebDriver driver = new ClientSideImplicitWaitWrapper(mockedDriver, 1, 100).getDriver();
    final Actions actions = new Actions(driver);
    WebElement element = driver.findElement(By.name("foo"));
    actions.click(element).perform();

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(5)).getCoordinates(); // there are 2 extra calls
    verify(mockedMouse, times(1)).click((Coordinates) anyObject());
  }

}
