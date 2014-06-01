/*
 * Copyright 2013 Alexei Barantsev
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
package ru.stqa.selenium.wrapper;

import org.junit.Test;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EventFiringWrapperTest {

  @Test
  public void canFireEventForGet() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.get("http://localhost/");

    verify(mockedDriver, times(1)).get("http://localhost/");
    verify(mockedListener, times(1)).beforeGet(mockedDriver, "http://localhost/");
    verify(mockedListener, times(1)).afterGet(mockedDriver, "http://localhost/");
  }

  @Test
  public void canFireEventForGetCurrentUrl() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.getCurrentUrl()).thenReturn("http://localhost/");

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    assertEquals(driver.getCurrentUrl(), "http://localhost/");

    verify(mockedDriver, times(1)).getCurrentUrl();
    verify(mockedListener, times(1)).beforeGetCurrentUrl(mockedDriver);
    verify(mockedListener, times(1)).afterGetCurrentUrl(mockedDriver, "http://localhost/");
  }

  @Test
  public void canFireEventForFindElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    WebElement result = driver.findElement(By.id("id"));

    assertEquals(result, mockedElement);

    verify(mockedDriver, times(1)).findElement(By.id("id"));
    verify(mockedListener, times(1)).beforeFindElement(mockedDriver, By.id("id"));
    verify(mockedListener, times(1)).afterFindElement(mockedDriver, mockedElement, By.id("id"));
  }

  @Test
  public void canFireEventForFindElements() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final List<WebElement> list = new ArrayList<WebElement>();
    list.add(mockedElement);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElements(By.id("id"))).thenReturn(list);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    List<WebElement> result = driver.findElements(By.id("id"));

    assertEquals(result, list);

    verify(mockedDriver, times(1)).findElements(By.id("id"));
    verify(mockedListener, times(1)).beforeFindElements(mockedDriver, By.id("id"));
    verify(mockedListener, times(1)).afterFindElements(mockedDriver, list, By.id("id"));
  }

  @Test
  public void canFireEventForGetWindowHandles() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final Set<String> handles = new HashSet<String>();
    handles.add("windows1");
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.getWindowHandles()).thenReturn(handles);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    Set<String> result = driver.getWindowHandles();

    assertEquals(result, handles);

    verify(mockedDriver, times(1)).getWindowHandles();
    verify(mockedListener, times(1)).beforeGetWindowHandles(mockedDriver);
    verify(mockedListener, times(1)).afterGetWindowHandles(mockedDriver, handles);
  }

  private static interface WebDriverWithJS extends WebDriver, JavascriptExecutor {}

  @Test
  public void canFireEventForExecuteScript() {
    final WebDriverWithJS mockedDriver = mock(WebDriverWithJS.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.executeScript("return arguments[0]", "test")).thenReturn("result");

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    Object result = ((JavascriptExecutor) driver).executeScript("return arguments[0]", "test");

    assertEquals(result, "result");

    verify(mockedDriver, times(1)).executeScript("return arguments[0]", "test");
    verify(mockedListener, times(1)).beforeExecuteScript(mockedDriver, "return arguments[0]", "test");
    verify(mockedListener, times(1)).afterExecuteScript(mockedDriver, "result", "return arguments[0]", "test");
  }

  @Test
  public void canFireEventForWebElementClick() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.findElement(By.id("id")).click();

    verify(mockedElement, times(1)).click();
    verify(mockedListener, times(1)).beforeClick(mockedElement);
    verify(mockedListener, times(1)).afterClick(mockedElement);
  }

  @Test
  public void canFireEventForWebElementSendKeys() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.findElement(By.id("id")).sendKeys("test");

    verify(mockedElement, times(1)).sendKeys("test");
    verify(mockedListener, times(1)).beforeSendKeys(mockedElement, "test");
    verify(mockedListener, times(1)).afterSendKeys(mockedElement, "test");
  }

  @Test
  public void canFireEventForWebElementGetText() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn("text");

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    String result = driver.findElement(By.id("id")).getText();

    assertEquals(result, "text");

    verify(mockedElement, times(1)).getText();
    verify(mockedListener, times(1)).beforeGetText(mockedElement);
    verify(mockedListener, times(1)).afterGetText(mockedElement, "text");
  }

  @Test
  public void canFireEventForWebElementGetAttribute() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id"))).thenReturn(mockedElement);
    when(mockedElement.getAttribute("value")).thenReturn("text");

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    String result = driver.findElement(By.id("id")).getAttribute("value");

    assertEquals(result, "text");

    verify(mockedElement, times(1)).getAttribute("value");
    verify(mockedListener, times(1)).beforeGetAttribute(mockedElement, "value");
    verify(mockedListener, times(1)).afterGetAttribute(mockedElement, "text", "value");
  }

  @Test
  public void canFireEventForRefresh() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.Navigation mockedNavigation = mock(WebDriver.Navigation.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.navigate()).thenReturn(mockedNavigation);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.navigate().refresh();

    verify(mockedNavigation, times(1)).refresh();
    verify(mockedListener, times(1)).beforeRefresh(mockedNavigation);
    verify(mockedListener, times(1)).afterRefresh(mockedNavigation);
  }

  @Test
  public void canFireEventForFindElementInElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElement(By.id("id2"))).thenReturn(mockedElement2);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    WebElement result = driver.findElement(By.id("id1")).findElement(By.id("id2"));

    assertEquals(result, mockedElement2);

    verify(mockedElement, times(1)).findElement(By.id("id2"));
    verify(mockedListener, times(1)).beforeFindElement(mockedDriver, By.id("id1"));
    verify(mockedListener, times(1)).afterFindElement(mockedDriver, mockedElement, By.id("id1"));
    verify(mockedListener, times(1)).beforeFindElement(mockedElement, By.id("id2"));
    verify(mockedListener, times(1)).afterFindElement(mockedElement, mockedElement2, By.id("id2"));
  }

  @Test
  public void canFireEventForFindElementsInElement() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class);
    final WebElement mockedElement2 = mock(WebElement.class);
    final List<WebElement> list = new ArrayList<WebElement>();
    list.add(mockedElement2);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.findElement(By.id("id1"))).thenReturn(mockedElement);
    when(mockedElement.findElements(By.id("id2"))).thenReturn(list);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    List<WebElement> result = driver.findElement(By.id("id1")).findElements(By.id("id2"));

    assertEquals(result, list);

    verify(mockedElement, times(1)).findElements(By.id("id2"));
    verify(mockedListener, times(1)).beforeFindElement(mockedDriver, By.id("id1"));
    verify(mockedListener, times(1)).afterFindElement(mockedDriver, mockedElement, By.id("id1"));
    verify(mockedListener, times(1)).beforeFindElements(mockedElement, By.id("id2"));
    verify(mockedListener, times(1)).afterFindElements(mockedElement, list, By.id("id2"));
  }

  @Test
  public void canFireEventForAlertAccept() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebDriver.TargetLocator mockedTarget = mock(WebDriver.TargetLocator.class);
    final Alert mockedAlert = mock(Alert.class);
    final WebDriverListener mockedListener = mock(WebDriverListener.class);
    when(mockedDriver.switchTo()).thenReturn(mockedTarget);
    when(mockedTarget.alert()).thenReturn(mockedAlert);

    EventFiringWrapper wrapper = new EventFiringWrapper(mockedDriver);
    wrapper.addListener(mockedListener);
    final WebDriver driver = wrapper.getDriver();

    driver.switchTo().alert().accept();

    verify(mockedAlert, times(1)).accept();
    verify(mockedListener, times(1)).beforeAccept(mockedAlert);
    verify(mockedListener, times(1)).afterAccept(mockedAlert);
  }

}
