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

import com.google.common.collect.Lists;
import org.openqa.selenium.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ClientSideImplicitWaitWrapperTest {

  private WebDriver getMockedDriver() {
    final WebDriver mockedDriver = mock(WebDriver.class);
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
  public void findElementShouldReturnEmptyListIfNoElementIsFound() {
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
    assertThat(alert, is(mockedAlert));
  }

}
