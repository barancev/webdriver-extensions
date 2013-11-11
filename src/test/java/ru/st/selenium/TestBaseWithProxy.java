package ru.st.selenium;

import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;

public class TestBaseWithProxy extends TestBase {
  
  protected ProxyServer proxy;
  protected WebDriver driver;

  @BeforeClass
  public void startProxy() throws Exception {
    proxy = new ProxyServer(8081);
    proxy.start();
    DesiredCapabilities caps = DesiredCapabilities.firefox();
    caps.setCapability(CapabilityType.PROXY, proxy.seleniumProxy());
    driver = new FirefoxDriver(caps);
  }
  
  public void stopProxy() throws Exception {
    //driver.quit();
    proxy.stop();
  }

}
