package ru.st.selenium.wrapper;

import com.google.common.base.Throwables;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AlertTolerantWebDriver extends WebDriverWrapper {

  private static void logUnhandledAlert(UnhandledAlertException target) {
    System.err.println("Unhandled alert: " + target.getAlertText());
    target.printStackTrace();
  }

  public AlertTolerantWebDriver(final WebDriver driver) {
    super(driver);
  }

//  @Override
//  protected Object callMethodGlobal(Object target, Method method, Object[] args) throws Throwable {
//    try {
//      return super.callMethodGlobal(target, method, args);
//    } catch (InvocationTargetException e) {
//      Throwable te = e.getTargetException();
//      if (te instanceof UnhandledAlertException) {
//        logUnhandledAlert((UnhandledAlertException) te);
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
  protected Object onErrorGlobal(Object target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    Throwable te = e.getTargetException();
    if (te instanceof UnhandledAlertException) {
      logUnhandledAlert((UnhandledAlertException) te);
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
