/*
 * Copyright 2013 Alexei Barantsev
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

class UnrestrictedStorage extends WebDriverFactoryInternal {

  private Map<String, WebDriver> keyToDriverMap = new HashMap<String, WebDriver>();
  private Map<WebDriver, String> driverToKeyMap = new WeakHashMap<WebDriver, String>();

  @Override
  public WebDriver getDriver(String hub, Capabilities capabilities) {
    String key = createKey(capabilities, hub);
    WebDriver driver = keyToDriverMap.get(key);
    if (driver == null) {
      driver = newDriver(hub, capabilities);
      keyToDriverMap.put(key, driver);
      driverToKeyMap.put(driver, key);

    } else {
      // Check the browser is alive
      try {
        driver.getCurrentUrl();
      } catch (Throwable t) {
        t.printStackTrace();
        driver = newDriver(hub, capabilities);
        keyToDriverMap.put(key, driver);
        driverToKeyMap.put(driver, key);
      }
    }

    return driver;
  }

  @Override
  public void dismissDriver(WebDriver driver) {
    if (driverToKeyMap.get(driver) == null) {
      throw new Error("The driver is not owned by the factory: " + driver);
    }
    driver.quit();
    String key = driverToKeyMap.remove(driver);
    keyToDriverMap.remove(key);
  }

  @Override
  public void dismissAll() {
    for (WebDriver driver : new HashSet<WebDriver>(driverToKeyMap.keySet())) {
      driver.quit();
      String key = driverToKeyMap.remove(driver);
      keyToDriverMap.remove(key);
    }
  }

  @Override
  public boolean isEmpty() {
    return driverToKeyMap.isEmpty();
  }

}
