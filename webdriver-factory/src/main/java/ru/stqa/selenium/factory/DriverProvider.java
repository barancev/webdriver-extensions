/*
 * Copyright 2014 Alexei Barantsev
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

package ru.stqa.selenium.factory;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface DriverProvider {

  /**
   * Creates a new driver with the desired capabilities, or returns null if the
   * capabilities does not match the provider's ability to create drivers.
   */
  WebDriver createDriver(Capabilities capabilities);

  static class Firefox implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.FIREFOX.equals(capabilities.getBrowserName())) {
        return new FirefoxDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class InternetExplorer implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.IE.equals(capabilities.getBrowserName())) {
        return new InternetExplorerDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class Chrome implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.CHROME.equals(capabilities.getBrowserName())) {
        return new ChromeDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class Opera implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.OPERA_BLINK.equals(capabilities.getBrowserName())) {
        return new OperaDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class Safari implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.SAFARI.equals(capabilities.getBrowserName())) {
        return new SafariDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class PhantomJS implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.PHANTOMJS.equals(capabilities.getBrowserName())) {
        return new PhantomJSDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class HtmlUnit implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      if (BrowserType.HTMLUNIT.equals(capabilities.getBrowserName())) {
        return new HtmlUnitDriver(capabilities);
      } else {
        return null;
      }
    }
  }

  static class ReflectionBased implements DriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      String browserType = capabilities.getBrowserName();
      try {
        Class<?> driverClass = WebDriverFactoryInternal.class.getClassLoader().loadClass(browserType);
        Constructor<?> constructor = driverClass.getConstructor(Capabilities.class);
        return (WebDriver) constructor.newInstance(capabilities);
      } catch (Exception e) {
        Logger.getLogger(DriverProvider.class.getName())
            .log(Level.INFO, "Unrecognized browser type: " + browserType, e);
        return null;
      }
    }
  }
}
