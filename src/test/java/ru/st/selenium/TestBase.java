package ru.st.selenium;

import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.webbitserver.WebServer;

public class TestBase {

  protected WebServer webServer = null;
  protected ProxyServer proxy = null;
  protected WebDriver driver = null;
  
  @AfterClass(alwaysRun = true)
  public void stopWebServer() {
    if (webServer != null) {
      webServer.stop();
    }
  }
  
  @AfterMethod(alwaysRun = true)
  public void tearDown() throws Exception {
    if (driver != null) {
      driver.quit();
      driver = null;
    }

    if (proxy != null) {
      proxy.stop();
      proxy = null;
    }
  }

  public String whereIs(String path) {
    return "http://localhost:8080/" + path;
  }
  
}
