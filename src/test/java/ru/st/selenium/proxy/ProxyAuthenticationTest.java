package ru.st.selenium.proxy;

import org.testng.annotations.Test;

import ru.st.selenium.TestBaseWithProxy;

public class ProxyAuthenticationTest extends TestBaseWithProxy {

  @Test
  public void test1() {
    driver.get(whereIs(STATIC));
  }

}
