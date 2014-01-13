/*
 * Copyright 2013 Alexei Barantsev
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package ru.stqa.selenium.wrapper;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class UnhandledAlertHandlingWrapper extends WebDriverWrapper {

  private List<UnhandledAlertHandler> handlers = Lists.newArrayList();

  public void registerAlertHandler(UnhandledAlertHandler handler) {
    handlers.add(handler);
  }

  public void deleteAllAlertHandlers() {
    handlers.clear();
  }

  private void handleUnhandledAlert(UnhandledAlertException ex) {
    for (UnhandledAlertHandler handler : handlers) {
      handler.handleUnhandledAlert(getWrappedDriver(), ex);
    }
  }

  public UnhandledAlertHandlingWrapper(final WebDriver driver) {
    super(driver);
  }

//  @Override
//  protected Object callMethodGlobal(Object target, Method method, Object[] args) throws Throwable {
//    try {
//      return super.callMethodGlobal(target, method, args);
//    } catch (InvocationTargetException e) {
//      Throwable te = e.getTargetException();
//      if (te instanceof UnhandledAlertException) {
//        handleUnhandledAlert((UnhandledAlertException) te);
//        // try again
//        try {
//          return super.callMethodGlobal(target, method, args);
//        } catch (InvocationTargetException e1) {
//          throw e1.getTargetException();
//        }
//      }
//      throw te;
//    }
//  }

  @Override
  protected Object onErrorGlobal(AbstractWrapper target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    Throwable te = e.getTargetException();
    if (te instanceof UnhandledAlertException) {
      handleUnhandledAlert((UnhandledAlertException) te);
      // try again
      try {
        return callMethodGlobal(target, method, args);
      } catch (InvocationTargetException e1) {
        throw Throwables.propagate(e1.getTargetException());
      }
    }
    throw Throwables.propagate(te);
  }
}
