package ru.st.selenium.wrapper;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AlertTolerantWebDriverTest {

  @Test
  public void testAlertIsIgnored() {
    WebDriver original = new FirefoxDriver();
    WebDriver driver = WebDriverWrapper.wrapDriver(original, AlertTolerantWebDriver.class);
    driver.get("http://localhost/test/alerts.html");
    driver.findElement(By.tagName("a")).click();
    System.out.println(driver.findElement(By.tagName("a")).getText());
    driver.quit();
  }

}
