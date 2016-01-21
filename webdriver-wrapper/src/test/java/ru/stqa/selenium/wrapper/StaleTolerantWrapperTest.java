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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StaleTolerantWrapperTest {

  //@Test
  public void testCanRediscoverAReplacedElement() {
    WebDriver original = new FirefoxDriver();

    StaleTolerantWrapper wrapper = new StaleTolerantWrapper(original);
    WebDriver driver = wrapper.getDriver();

    driver.get("http://fiddle.jshell.net/barancev/5Z9bd/show/light/");
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button2 = driver.findElement(By.id("b2"));

    button1.click();
    button2.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button2"));

    driver.quit();
  }

  //@Test
  public void testCanRediscoverAReplacedChildElement() {
    WebDriver original = new FirefoxDriver();

    StaleTolerantWrapper wrapper = new StaleTolerantWrapper(original);
    WebDriver driver = wrapper.getDriver();

    driver.get("http://fiddle.jshell.net/barancev/5Z9bd/show/light/");
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button3 = driver.findElement(By.id("div3")).findElement(By.id("b3"));

    button1.click();
    button3.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button3"));

    driver.quit();
  }

  //@Test
  public void testCanRediscoverAReplacedSubtree() {
    WebDriver original = new FirefoxDriver();

    StaleTolerantWrapper wrapper = new StaleTolerantWrapper(original);
    WebDriver driver = wrapper.getDriver();

    driver.get("http://fiddle.jshell.net/barancev/5Z9bd/show/light/");
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button4 = driver.findElement(By.id("div4")).findElement(By.id("b4"));

    button1.click();
    button4.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button4"));

    driver.quit();
  }

}
