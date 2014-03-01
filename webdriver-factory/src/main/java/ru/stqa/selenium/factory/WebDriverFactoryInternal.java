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

import com.opera.core.systems.OperaDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;

abstract class WebDriverFactoryInternal {

  public abstract WebDriver getDriver(String hub, Capabilities capabilities);
  public abstract void dismissDriver(WebDriver driver);
  public abstract void dismissAll();
  public abstract boolean isEmpty();

  private String defaultHub = null;

  public void setDefaultHub(String defaultHub) {
    this.defaultHub = defaultHub;
  }

  public WebDriver getDriver(Capabilities capabilities) {
    return getDriver(defaultHub, capabilities);
  }

  protected static String createKey(Capabilities capabilities, String hub) {
    return capabilities.toString() + ":" + hub;
  }

  protected static WebDriver newDriver(String hub, Capabilities capabilities) {
    return (hub == null)
        ? createLocalDriver(capabilities)
        : createRemoteDriver(hub, capabilities);
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
    if (browserType.equals(BrowserType.FIREFOX))
      return new FirefoxDriver(capabilities);
    if (browserType.equals(BrowserType.IE))
      return new InternetExplorerDriver(capabilities);
    if (browserType.equals(BrowserType.CHROME))
      return new ChromeDriver(capabilities);
    if (browserType.equals(BrowserType.OPERA))
      return new OperaDriver(capabilities);
    if (browserType.equals(BrowserType.SAFARI))
      return new SafariDriver(capabilities);
    if (browserType.equals(BrowserType.PHANTOMJS))
      return new PhantomJSDriver(capabilities);
    if (browserType.equals(BrowserType.HTMLUNIT))
      return new HtmlUnitDriver(capabilities);
    throw new Error("Unrecognized browser type: " + browserType);
  }

}
