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
package ru.stqa.selenium.wrapper;

import com.google.common.base.Throwables;
import org.openqa.selenium.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StaleTolerantWrapper extends WebDriverWrapper {

  public StaleTolerantWrapper(final WebDriver driver) {
    super(driver);
  }

  @Override
  protected Class<? extends WebElementWrapper> getElementWrapperClass() {
    return StaleTolerantWebElementWrapper.class;
  }

  @Override
  protected void afterMethod(Method method, Object res, Object[] args) {
    afterMethodGlobal(this, method, res, args);
  }

  @Override
  protected void afterMethodGlobal(AbstractWrapper target, Method method, Object res, Object[] args) {
    super.afterMethodGlobal(target, method, unwrap(res), args);
    if (method.getName().equals("findElement")) {
      Rediscoverable elementWrapper = (Rediscoverable) res;
      elementWrapper.setSearchContext((SearchContext) target.wrapOriginal());
      elementWrapper.setLocator((By) args[0]);
    }
  }

  @Override
  protected Object onErrorGlobal(AbstractWrapper target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    Throwable te = e.getTargetException();
    if (te instanceof StaleElementReferenceException) {
      StaleTolerantWebElementWrapper elementWrapper = (StaleTolerantWebElementWrapper) target;
      try {
        WebElement newElement = elementWrapper.getSearchContext().findElement(elementWrapper.getLocator());
        elementWrapper.setWrappedOriginal(newElement);
        try {
          return callMethodGlobal(target, method, args);
        } catch (InvocationTargetException e1) {
          throw Throwables.propagate(e1.getTargetException());
        }
      } catch (NoSuchElementException ex) {
        throw Throwables.propagate(te);
      }
    }
    throw Throwables.propagate(te);
  }

  public interface Rediscoverable {
    void setSearchContext(SearchContext searchContext);
    SearchContext getSearchContext();
    void setLocator(By locator);
    By getLocator();
  }

  public class StaleTolerantWebElementWrapper extends WebElementWrapper implements Rediscoverable {

    private SearchContext searchContext;
    private By locator;

    public StaleTolerantWebElementWrapper(WebElement element) {
      super(StaleTolerantWrapper.this, element);
    }

    @Override
    protected void afterMethod(Method method, Object res, Object[] args) {
      afterMethodGlobal(this, method, res, args);
    }

    @Override
    public void setSearchContext(SearchContext searchContext) {
      this.searchContext = searchContext;
    }

    @Override
    public SearchContext getSearchContext() {
      return searchContext;
    }

    @Override
    public void setLocator(By locator) {
      this.locator = locator;
    }

    @Override
    public By getLocator() {
      return locator;
    }
  }

}
