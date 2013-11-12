package ru.st.selenium.proxy;

import java.io.File;

import net.lightbody.bmp.proxy.ProxyServer;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.authentication.BasicAuthenticationHandler;
import org.webbitserver.handler.authentication.InMemoryPasswords;

import ru.st.selenium.TestBase;

public class ProxyAuthenticationTest extends TestBase {

  @Test
  public void testAuthPassed() throws Exception {
    webServer = WebServers.createWebServer(8080)
        .add(new BasicAuthenticationHandler(
            new InMemoryPasswords().add("testuser", "passwd")))
        .add(new StaticFileHandler(new File("src/test/resources")))
        .start().get();

    proxy = new ProxyServer(8081);
    proxy.start();
    proxy.autoBasicAuthorization("", "testuser", "passwd");

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability(CapabilityType.PROXY, proxy.seleniumProxy());

    driver = new FirefoxDriver(caps);
    driver.get("http://localhost:8080/test.html");

    assertThat(driver.findElement(By.tagName("body")).getText(), is("Test passed"));
  }
  
}
