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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

abstract class WebDriverFactoryInternal {

  public abstract WebDriver getDriver(String hub, Capabilities capabilities);
  public abstract void dismissDriver(WebDriver driver);
  public abstract void dismissAll();
  public abstract boolean isEmpty();

  private String defaultHub = null;

  private LinkedList<DriverProvider> driverProviders
      = new LinkedList<DriverProvider>();
  {
    driverProviders.add(new DriverProvider.Firefox());
    driverProviders.add(new DriverProvider.InternetExplorer());
    driverProviders.add(new DriverProvider.Chrome());
    driverProviders.add(new DriverProvider.Opera());
    driverProviders.add(new DriverProvider.Safari());
    driverProviders.add(new DriverProvider.PhantomJS());
    driverProviders.add(new DriverProvider.HtmlUnit());
    driverProviders.add(new DriverProvider.ReflectionBased());
  }

  private LinkedList<RemoteDriverProvider> remoteDriverProviders
      = new LinkedList<RemoteDriverProvider>();
  {
    remoteDriverProviders.add(new RemoteDriverProvider.Default());
  }

  void addDriverProvider(DriverProvider provider) {
    driverProviders.addFirst(provider);
  }

  void addRemoteDriverProvider(RemoteDriverProvider provider) {
    remoteDriverProviders.addFirst(provider);
  }

  public void setDefaultHub(String defaultHub) {
    this.defaultHub = defaultHub;
  }

  public WebDriver getDriver(Capabilities capabilities) {
    return getDriver(defaultHub, capabilities);
  }

  protected String createKey(Capabilities capabilities, String hub) {
    return capabilities.toString() + ":" + hub;
  }

  protected WebDriver newDriver(String hub, Capabilities capabilities) {
    return (hub == null)
        ? createLocalDriver(capabilities)
        : createRemoteDriver(hub, capabilities);
  }

  private WebDriver createRemoteDriver(String hub, Capabilities capabilities) {
    for (RemoteDriverProvider provider : remoteDriverProviders) {
      WebDriver driver = provider.createDriver(hub, capabilities);
      if (driver != null) {
        return driver;
      }
    }
    throw new Error("Can't find remote driver provider for capabilities " + capabilities);
  }

  private WebDriver createLocalDriver(Capabilities capabilities) {
    for (DriverProvider provider : driverProviders) {
      WebDriver driver = provider.createDriver(capabilities);
      if (driver != null) {
        return driver;
      }
    }
    throw new Error("Can't find driver provider for capabilities " + capabilities);
  }
}
