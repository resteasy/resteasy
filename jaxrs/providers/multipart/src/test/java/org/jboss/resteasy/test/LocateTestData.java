package org.jboss.resteasy.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Intellij doesn't build test/resources paths or add them to classpath so I built a bridge to find test-data
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocateTestData
{
   public static File getTestData(String relativePath)
   {
      if (relativePath.startsWith("/")) relativePath = relativePath.substring(1);
      String resourcePath = LocateTestData.class.getName().replace('.', '/') + ".class";
      URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
      if (url == null) return null;
      String urlStr = url.toString();
      urlStr = urlStr.substring(0, urlStr.lastIndexOf("target/test-classes/" + resourcePath));
      urlStr += "src/test/test-data/" + relativePath;
      System.out.println("URLSTR: " + urlStr);
      try
      {
         return new File(new URL(urlStr).toURI());
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }


   }
}