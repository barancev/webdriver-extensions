package ru.st.selenium.factory;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.opera.core.systems.OperaDriver;

public class WebDriverFactory {

  // Factory settings

  private static String defaultHub = null;

  private static int restartFrequency = Integer.MAX_VALUE;

  public static void setDefaultHub(String newDefaultHub) {
    defaultHub = newDefaultHub;
  }

  public static void setRestartFrequency(int newRestartFrequency) {
    restartFrequency = newRestartFrequency;
  }

  // Factory

  private static String key = null;
  private static int count = 0;
  private static WebDriver driver;

  public static WebDriver getDriver(String hub, Capabilities capabilities) {
    count++;
    // 1. WebDriver instance is not created yet
    if (driver == null) {
      return newWebDriver(hub, capabilities);
    }
    // 2. Different flavour of WebDriver is required
    String newKey = capabilities.toString() + ":" + hub;
    if (!newKey.equals(key)) {
      dismissDriver();
      key = newKey;
      return newWebDriver(hub, capabilities);
    }
    // 3. Browser is dead
    try {
      driver.getCurrentUrl();
    } catch (Throwable t) {
      t.printStackTrace();
      return newWebDriver(hub, capabilities);
    }
    // 4. It's time to restart
    if (count >= restartFrequency) {
      dismissDriver();
      return newWebDriver(hub, capabilities);
    }
    // 5. Just use existing WebDriver instance
    return driver;
  }

  public static WebDriver getDriver(Capabilities capabilities) {
    return getDriver(defaultHub, capabilities);
  }

  public static void dismissDriver() {
    if (driver != null) {
      try {
        driver.quit();
        driver = null;
        key = null;
      } catch (WebDriverException ex) {
        // it can already be dead or unreachable
      }
    }
  }

  // Factory internals

  private static WebDriver newWebDriver(String hub, Capabilities capabilities) {
    driver = (hub == null)
        ? createLocalDriver(capabilities)
        : createRemoteDriver(hub, capabilities);
    key = capabilities.toString() + ":" + hub;
    count = 0;
    return driver;
  }

  private static WebDriver createRemoteDriver(String hub, Capabilities capabilities) {
    try {
      return new RemoteWebDriver(new URL(hub), capabilities);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new Error("Could not connect to WebDriver hub", e);
    }
  }

  private static WebDriver createLocalDriver(Capabilities capabilities) {
    String browserType = capabilities.getBrowserName();
    if (browserType.equals("firefox"))
      return new FirefoxDriver(capabilities);
    if (browserType.startsWith("internet explorer"))
      return new InternetExplorerDriver(capabilities);
    if (browserType.equals("chrome"))
      return new ChromeDriver(capabilities);
    if (browserType.equals("opera"))
      return new OperaDriver(capabilities);
    if (browserType.equals("safari"))
      return new SafariDriver(capabilities);
    throw new Error("Unrecognized browser type: " + browserType);
  }

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        dismissDriver();
      }
    });
  }
}
