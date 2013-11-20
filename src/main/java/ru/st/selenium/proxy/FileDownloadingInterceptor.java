package ru.st.selenium.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class FileDownloadingInterceptor implements HttpResponseInterceptor {
  
  private Set<String> contentTypes = new HashSet<String>();
  private File tempDir = null;
  private File tempFile = null;
  
  public FileDownloadingInterceptor addContentType(String contentType) {
    contentTypes.add(contentType);
    return this;
  }

  @Override
  public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
    String contentType = response.getFirstHeader("Content-Type").getValue();
    if (contentTypes.contains(contentType)) {
      String postfix = contentType.substring(contentType.indexOf('/') + 1);
      tempFile = File.createTempFile("downloaded", "."+postfix, tempDir);
      tempFile.deleteOnExit();

      FileOutputStream outputStream = new FileOutputStream(tempFile);
      outputStream.write(EntityUtils.toByteArray(response.getEntity()));
      outputStream.close();

      response.removeHeaders("Content-Type");
      response.removeHeaders("Content-Encoding");
      response.removeHeaders("Content-Disposition");

      response.addHeader("Content-Type", "text/html");
      response.addHeader("Content-Length", "" + tempFile.getAbsolutePath().length());
      response.setEntity(new StringEntity(tempFile.getAbsolutePath()));
    }
  }

}
