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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Method;
import java.util.List;

public class HighlightingWrapper extends WebDriverWrapper {

  private String cssElementId = "highlighting-wrapper-id";
  private String actionStyle = "border: 2px solid red";
  private String actionClass = "highlighting-wrapper-action";
  private String foundStyle = "border: 2px solid blue";
  private String foundClass = "highlighting-wrapper-found";

  private long pause = 2000;

  public HighlightingWrapper(final WebDriver driver) {
    super(driver);
  }

  public HighlightingWrapper(final WebDriver driver, long pause) {
    super(driver);
    this.pause = pause;
  }

  public HighlightingWrapper(final WebDriver driver, final String actionStyle, final String foundStyle) {
    super(driver);
    this.actionStyle = actionStyle;
    this.foundStyle = foundStyle;
  }

  public HighlightingWrapper(final WebDriver driver, long pause, final String actionStyle, final String foundStyle) {
    super(driver);
    this.pause = pause;
    this.actionStyle = actionStyle;
    this.foundStyle = foundStyle;
  }

  @Override
  protected void beforeMethodGlobal(AbstractWrapper target, Method method, Object[] args) {
    if (target.getWrappedOriginal() instanceof WebElement) {
      final WebElement element = (WebElement) target.getWrappedOriginal();
      highlight(element, actionClass);
    }
    super.beforeMethodGlobal(target, method, args);
  }

  @Override
  protected void afterMethodGlobal(AbstractWrapper target, Method method, Object res, Object[] args) {
    if (res != null) {
      if (res instanceof WebElement) {
        WebElement element = (WebElement) res;
        highlight(element, foundClass);
      } else if (res instanceof List<?>) {

      }
    }
    super.afterMethodGlobal(target, method, res, args);
  }

  private void highlight(WebElement element, String cls) {
    addStyleToHeader();
    try {
      addClass(element, cls);
      pause();
    } finally {
      removeClass(element, cls);
    }
  }

  private String addStyleToHeader() {
    String script = "if (document.getElementById('" + cssElementId + "')) return; "+
        "var highlightWrapperStyleElement = document.createElement('style'); " +
        "highlightWrapperStyleElement.id = '"+ cssElementId +"'; " +
        "highlightWrapperStyleElement.type = 'text/css'; " +
        "var highlightWrapperStyle = '." + actionClass + " {" + actionStyle + "} ." + foundClass + " {" + foundStyle + "}'; " +
        "if (highlightWrapperStyleElement.styleSheet) { " +
        "highlightWrapperStyleElement.styleSheet.cssText = highlightWrapperStyle; " +
        "} else { highlightWrapperStyleElement.appendChild(document.createTextNode(highlightWrapperStyle)); } " +
        "var headHighlightWrapper = document.getElementsByTagName('head')[0]; " +
        "headHighlightWrapper.appendChild(highlightWrapperStyleElement);";
    ((JavascriptExecutor) getWrappedOriginal()).executeScript(script);
    return null;
  }

  private void addClass(WebElement element, String cls) {
    ((JavascriptExecutor) getWrappedOriginal()).executeScript(
        "arguments[0].className += ' ' + arguments[1]", element, cls);
  }

  private void removeClass(WebElement element, String cls) {
    ((JavascriptExecutor) getWrappedOriginal()).executeScript(
        "arguments[0].className = arguments[0].className.replace(arguments[1], '')", element, cls);
  }

  private void pause() {
    try {
      Thread.sleep(pause);
    } catch (InterruptedException e) {
    }
  }

}
