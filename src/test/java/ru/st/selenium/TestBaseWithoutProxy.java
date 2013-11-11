package ru.st.selenium;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBaseWithoutProxy extends TestBase {
  
  protected WebDriver driver;

  @BeforeClass
  public void startProxy() {
    driver = new FirefoxDriver();
  }
  
  @AfterClass
  public void stopProxy() {
    driver.quit();
  }

}
