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
import org.junit.Before;
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

  private TestingClock clock;
  private WebDriver mockedDriver;
  private WebDriver driver;

  @Before
  public void setUp() {
    clock = new TestingClock();
    mockedDriver = getMockedDriver();
    driver = new ClientSideImplicitWaitWrapper(mockedDriver, clock, clock, 1, 100).getDriver();
  }

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
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    WebElement element = driver.findElement(By.name("foo"));

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    assertThat(element, equalTo(mockedElement));
  }

  @Test
  public void findElementShouldThrowIfElementIsNotFound() {
    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class);

    try {
      driver.findElement(By.name("foo"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(11)).findElement(By.name("foo"));
  }

  @Test
  public void findElementsShouldImplicitlyWaitForAtLeastOneElementToBePresent() {
    final WebElement mockedElement1 = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> mockedElements = Lists.newArrayList(mockedElement1, mockedElement2);

    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(new ArrayList<WebElement>())
        .thenReturn(mockedElements);

    List<WebElement> elements = driver.findElements(By.name("foo"));

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    assertThat(elements, equalTo(mockedElements));
  }

  @Test
  public void findElementsShouldReturnEmptyListIfNoElementIsFound() {
    when(mockedDriver.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>());

    List<WebElement> elements = driver.findElements(By.name("foo"));

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(11)).findElements(By.name("foo"));
    assertThat(elements.size(), is(0));
  }

  @Test
  public void clickShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

   doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).click();

    driver.findElement(By.name("foo")).click();

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).click();
  }

  @Test
  public void clickShouldThrowIfTheElementIsNotVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).click();

    try {
      driver.findElement(By.name("foo")).click();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).click();
  }

  @Test
  public void submitShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).submit();

    driver.findElement(By.name("foo")).submit();

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).submit();
  }

  @Test
  public void submitShouldThrowIfTheElementIsNotVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).submit();

    try {
      driver.findElement(By.name("foo")).submit();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).submit();
  }

  @Test
  public void sendKeysShouldImplicitlyWaitForTheElementToBeVisible() {
    final String text = "To be or not to be";
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).sendKeys(text);

    driver.findElement(By.name("foo")).sendKeys(text);

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).sendKeys(text);
  }

  @Test
  public void sendKeysShouldThrowIfTheElementIsNotVisible() {
    final String text = "To be or not to be";
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).sendKeys(text);

    try {
      driver.findElement(By.name("foo")).sendKeys(text);
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).sendKeys(text);
  }

  @Test
  public void clearShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .doThrow(ElementNotVisibleException.class)
        .doNothing()
        .when(mockedElement).clear();

    driver.findElement(By.name("foo")).clear();

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).clear();
  }

  @Test
  public void clearShouldThrowIfTheElementIsNotVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    doThrow(ElementNotVisibleException.class)
        .when(mockedElement).clear();

    try {
      driver.findElement(By.name("foo")).clear();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).clear();
  }

  @Test
  public void isSelectedShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    boolean selected = driver.findElement(By.name("foo")).isSelected();

    assertThat(clock.now(), is(200L));
    assertThat(selected, is(true));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isSelected();
  }

  @Test
  public void isSelectedShouldImplicitlyWaitForTheElementToBeVisibleEvenIfTheElementIsNotSelected() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(false);

    boolean selected = driver.findElement(By.name("foo")).isSelected();

    assertThat(clock.now(), is(200L));
    assertThat(selected, is(false));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isSelected();
  }

  @Test
  public void isSelectedShouldThrowIfTheElementIsNotVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isSelected())
        .thenThrow(ElementNotVisibleException.class);

    try {
      driver.findElement(By.name("foo")).isSelected();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).isSelected();
  }

  @Test
  public void isEnabledShouldImplicitlyWaitForTheElementToBeVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(true);

    boolean selected = driver.findElement(By.name("foo")).isEnabled();

    assertThat(clock.now(), is(200L));
    assertThat(selected, is(true));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isEnabled();
  }

  @Test
  public void isEnabledShouldImplicitlyWaitForTheElementToBeVisibleEvenIfTheElementIsNotEnabled() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class)
        .thenThrow(ElementNotVisibleException.class)
        .thenReturn(false);

    boolean selected = driver.findElement(By.name("foo")).isEnabled();

    assertThat(clock.now(), is(200L));
    assertThat(selected, is(false));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).isEnabled();
  }

  @Test
  public void isEnabledShouldThrowIfTheElementIsNotVisible() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenReturn(mockedElement);

    when(mockedElement.isEnabled())
        .thenThrow(ElementNotVisibleException.class);

    try {
      driver.findElement(By.name("foo")).isEnabled();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(ElementNotVisibleException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).isEnabled();
  }

  @Test
  public void findElementInAnotherElementShouldImplicitlyWaitForAnElementToBePresent() {
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

    WebElement element = driver.findElement(By.name("foo"));
    WebElement element2 = element.findElement(By.name("bar"));

    assertThat(clock.now(), is(400L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).findElement(By.name("bar"));
    assertThat(element2, equalTo(mockedElement2));
  }

  @Test
  public void findElementInAnotherElementShouldThrowIfElementIsNotFound() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElement(By.name("bar")))
        .thenThrow(NoSuchElementException.class);

    WebElement element = driver.findElement(By.name("foo"));

    try {
      element.findElement(By.name("bar"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    assertThat(clock.now(), is(1200L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).findElement(By.name("bar"));
  }

  @Test
  public void findElementsInAnotherElementShouldImplicitlyWaitForAtLeastOneElementToBePresent() {
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

    WebElement element = driver.findElement(By.name("foo"));
    List<WebElement> elements = element.findElements(By.name("bar"));

    assertThat(clock.now(), is(400L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(3)).findElements(By.name("bar"));
    assertThat(elements, equalTo(mockedElements));
  }

  @Test
  public void findElementsInAnotherElementShouldReturnEmptyListIfNoElementIsFound() {
    final WebElement mockedElement = mock(WebElement.class);

    when(mockedDriver.findElement(By.name("foo")))
        .thenThrow(NoSuchElementException.class)
        .thenThrow(NoSuchElementException.class)
        .thenReturn(mockedElement);

    when(mockedElement.findElements(By.name("foo")))
        .thenReturn(new ArrayList<WebElement>());

    WebElement element = driver.findElement(By.name("foo"));
    List<WebElement> elements = element.findElements(By.name("foo"));

    assertThat(clock.now(), is(1200L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement, times(11)).findElements(By.name("foo"));
    assertThat(elements.size(), is(0));
  }

  @Test
  public void findElementChainShouldImplicitlyWaitForAnElementToBePresent() {
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

    WebElement element1 = driver.findElement(By.name("foo"));
    WebElement element2 = element1.findElement(By.name("bar"));
    WebElement element3 = element2.findElement(By.name("baz"));

    assertThat(clock.now(), is(600L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement1, times(3)).findElement(By.name("bar"));
    verify(mockedElement2, times(3)).findElement(By.name("baz"));
    assertThat(element3, equalTo(mockedElement3));
  }

  @Test
  public void findElementChainShouldThrowIfElementIsNotFound() {
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

    WebElement element1 = driver.findElement(By.name("foo"));
    WebElement element2 = element1.findElement(By.name("bar"));

    try {
      element2.findElement(By.name("baz"));
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchElementException.class));
    }

    assertThat(clock.now(), is(1400L));
    verify(mockedDriver, times(3)).findElement(By.name("foo"));
    verify(mockedElement1, times(3)).findElement(By.name("bar"));
    verify(mockedElement2, times(11)).findElement(By.name("baz"));
  }

  @Test
  public void findElementsChainShouldImplicitlyWaitForAnElementToBePresent() {
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

    List<WebElement> elements1 = driver.findElements(By.name("foo"));
    List<WebElement> elements2 = elements1.get(0).findElements(By.name("bar"));
    List<WebElement> elements3 = elements2.get(0).findElements(By.name("baz"));

    assertThat(clock.now(), is(600L));
    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    verify(mockedElement1, times(3)).findElements(By.name("bar"));
    verify(mockedElement2, times(3)).findElements(By.name("baz"));
    assertThat(elements3.get(0), equalTo(mockedElement3));
  }

  @Test
  public void findElementsChainShouldThrowIfElementIsNotFound() {
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

    List<WebElement> elements1 = driver.findElements(By.name("foo"));
    List<WebElement> elements2 = elements1.get(0).findElements(By.name("bar"));
    List<WebElement> elements3 = elements2.get(0).findElements(By.name("baz"));

    assertThat(clock.now(), is(1400L));
    verify(mockedDriver, times(3)).findElements(By.name("foo"));
    verify(mockedElement1, times(3)).findElements(By.name("bar"));
    verify(mockedElement2, times(11)).findElements(By.name("baz"));
    assertThat(elements3.size(), is(0));
  }

  @Test
  public void switchToAlertShouldImplicitlyWaitForAnAlertToBePresent() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert())
        .thenThrow(NoAlertPresentException.class)
        .thenThrow(NoAlertPresentException.class)
        .thenReturn(mockedAlert);

    Alert alert = driver.switchTo().alert();

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).alert();
    assertThat(alert, is(mockedAlert));
  }

  @Test
  public void switchToAlertShouldThrowIfThereIsNoAlert() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.alert())
        .thenThrow(NoAlertPresentException.class);

    try {
      driver.switchTo().alert();
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoAlertPresentException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).alert();
  }

  @Test
  public void switchToFrameByIndexShouldImplicitlyWaitForAFrameToBePresent() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame(1))
        .thenThrow(NoSuchFrameException.class)
        .thenThrow(NoSuchFrameException.class)
        .thenReturn(mockedDriver);

    WebDriver newDriver = driver.switchTo().frame(1);

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).frame(1);
    assertThat(driver, is(newDriver));
  }

  @Test
  public void switchToFrameByIndexShouldThrowIfThereIsNoFrame() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame(1))
        .thenThrow(NoSuchFrameException.class);

    try {
      driver.switchTo().frame(1);
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchFrameException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).frame(1);
  }

  @Test
  public void switchToFrameByNameShouldImplicitlyWaitForAFrameToBePresent() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame("myname"))
        .thenThrow(NoSuchFrameException.class)
        .thenThrow(NoSuchFrameException.class)
        .thenReturn(mockedDriver);

    WebDriver newDriver = driver.switchTo().frame("myname");

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(3)).frame("myname");
    assertThat(driver, is(newDriver));
  }

  @Test
  public void switchToFrameByNameShouldThrowIfThereIsNoFrame() {
    final WebDriver.TargetLocator mockedSwitch = mock(WebDriver.TargetLocator.class);

    when(mockedDriver.switchTo()).thenReturn(mockedSwitch);
    when(mockedSwitch.frame("myname"))
        .thenThrow(NoSuchFrameException.class);

    try {
      driver.switchTo().frame("myname");
      fail("Exception expected");
    } catch (Throwable t) {
      assertThat(t, instanceOf(NoSuchFrameException.class));
    }

    assertThat(clock.now(), is(1000L));
    verify(mockedDriver, times(1)).switchTo();
    verify(mockedSwitch, times(11)).frame("myname");
  }

  private interface LocatableElement extends WebElement, Locatable {}

  @Test
  public void interactionsClickShouldImplicitlyWaitForTheElementToBeVisible() {
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

    final Actions actions = new Actions(driver);
    WebElement element = driver.findElement(By.name("foo"));
    actions.click(element).perform();

    assertThat(clock.now(), is(200L));
    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(5)).getCoordinates(); // there are 2 extra calls
    verify(mockedMouse, times(1)).click((Coordinates) anyObject());
  }

}
