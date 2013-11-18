package ru.st.selenium.proxy;

import java.io.File;

import net.lightbody.bmp.proxy.ProxyServer;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.apache.http.HttpResponseInterceptor;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import ru.st.selenium.TestBase;

public class ProxyDownoadingTest extends TestBase {

  @Test
  public void testAuthPassed() throws Exception {
    webServer = WebServers.createWebServer(8080)
        .add(new StaticFileHandler(new File("src/test/resources")))
        .start().get();

    proxy = new ProxyServer(8081);
    proxy.start();
    proxy.setCaptureContent(true);
    proxy.setCaptureBinaryContent(true);
    FileDownloadingInterceptor interceptor = new FileDownloadingInterceptor()
        .addContentType("application/pdf");
    proxy.addResponseInterceptor(interceptor);

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability(CapabilityType.PROXY, proxy.seleniumProxy());

    driver = new FirefoxDriver(caps);
    driver.get("http://samplepdf.com/sample.pdf");

    assertThat(driver.findElement(By.tagName("body")).getText(), is("Test passed"));
  }
  
}
