package ru.st.selenium.wrapper;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class UnhandledAlertHandlingWrapperTest {

  public static class SimpleUnhandledAlertHandler implements UnhandledAlertHandler {
    public String alertText;

    @Override
    public void handleUnhandledAlert(WebDriver driver, UnhandledAlertException ex) {
      alertText = ex.getAlertText();
    }
  }

  @Test
  public void testAlertIsIgnored() {
    WebDriver original = new FirefoxDriver();

    SimpleUnhandledAlertHandler handler = new SimpleUnhandledAlertHandler();
    UnhandledAlertHandlingWrapper wrapper = new UnhandledAlertHandlingWrapper(original);
    wrapper.registerAlertHandler(handler);
    WebDriver driver = wrapper.getDriver();

    driver.get("http://localhost/test/alerts.html");
    driver.findElement(By.tagName("a")).click();

    String elementText = driver.findElement(By.tagName("a")).getText();
    assertThat(elementText, is("click me"));

    assertThat(handler.alertText, is("cheese"));

    driver.quit();
  }

}
