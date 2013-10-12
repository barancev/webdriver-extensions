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
package ru.st.selenium.wrapper;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;

/**
 * Simple {@link WrapsElement} delegating all calls to the wrapped {@link WebElement}.
 * The methods {@link WebDriverWrapper#wrapElement(WebElement)}/{@link WebDriverWrapper#wrapElements(List<WebElement>)} will
 * be called on the related {@link WebDriverWrapper} to wrap the elements returned by {@link #findElement(By)}/{@link #findElements(By)}.
 */
public class WebElementWrapper implements WebElement, WrapsElement {
  private WebElement wrappedElement;
  private final WebDriverWrapper driverWrapper;

  public WebElementWrapper(final WebDriverWrapper driverWrapper, final WebElement element) {
    wrappedElement = element;
    this.driverWrapper = driverWrapper;
  }

  public WebElement getWrappedElement() {
    return wrappedElement;
  }

  /**
   * Get the related {@link WebDriverWrapper} that will be used to wrap elements.
   */
  protected WebDriverWrapper getDriverWrapper() {
    return driverWrapper;
  }

  /**
   * Changes the wrapped element
   * @param newElement the new wrapped element
   */
  protected void setWrappedElement(final WebElement newElement) {
    wrappedElement = newElement;
  }

  public void click() {
    getWrappedElement().click();
  }

  public void submit() {
    getWrappedElement().submit();
  }

  public void sendKeys(final CharSequence... keysToSend) {
    getWrappedElement().sendKeys(keysToSend);
  }

  public void clear() {
    getWrappedElement().clear();
  }

  public String getTagName() {
    return getWrappedElement().getTagName();
  }

  public String getAttribute(final String name) {
    return getWrappedElement().getAttribute(name);
  }

  public boolean isSelected() {
    return getWrappedElement().isSelected();
  }

  public boolean isEnabled() {
    return getWrappedElement().isEnabled();
  }

  public String getText() {
    return getWrappedElement().getText();
  }

  public List<WebElement> findElements(final By by) {
    final List<WebElement> elements = getWrappedElement().findElements(by);
    return getDriverWrapper().wrapElements(elements);
  }

  public WebElement findElement(final By by) {
    return getDriverWrapper().wrapElement(getWrappedElement().findElement(by));
  }

  public boolean isDisplayed() {
    return getWrappedElement().isDisplayed();
  }

  public Point getLocation() {
    return getWrappedElement().getLocation();
  }

  public Dimension getSize() {
    return getWrappedElement().getSize();
  }

  public String getCssValue(final String propertyName) {
    return getWrappedElement().getCssValue(propertyName);
  }
}
