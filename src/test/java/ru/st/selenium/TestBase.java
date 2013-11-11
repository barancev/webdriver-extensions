package ru.st.selenium;

import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

public class TestBase {

  protected WebServer webServer;
  protected static String STATIC = "/";

  @BeforeClass
  public void startWebServer() {
    webServer = WebServers.createWebServer(8080);
    StaticFileHandler staticFileHandler = new StaticFileHandler(new File("."));
    staticFileHandler.enableDirectoryListing(true);
    staticFileHandler.welcomeFile(".");
    webServer.add(STATIC, staticFileHandler);
    webServer.start();
  }
  
  @AfterClass
  public void stopWebServer() {
    webServer.stop();
  }
  
  public String whereIs(String path) {
    return "http://127.0.0.1:8080" + path;
  }
  
}
