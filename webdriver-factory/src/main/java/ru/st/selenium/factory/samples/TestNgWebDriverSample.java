package ru.st.selenium.factory.samples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.st.selenium.factory.WebDriverFactory;

public class TestNgWebDriverSample {

  WebDriver driver;

  @BeforeMethod
  public void startBrowser() {
    driver = WebDriverFactory.getDriver(DesiredCapabilities.firefox());
  }

  @AfterSuite
  public void stopBrowser() {
    WebDriverFactory.dismissDriver();
  }

  @Test
  public void test1() {
    doSomething();
  }

  @Test
  public void test2() {
    doSomething();
  }

  @Test
  public void test3() {
    doSomething();
  }

  private void doSomething() {
    driver.get("http://seleniumhq.org/");
    driver.findElement(By.name("q")).sendKeys("selenium");
    driver.findElement(By.id("submit")).click();
    new WebDriverWait(driver, 30).until(
        ExpectedConditions.titleContains("Google Custom Search"));
  }

}
