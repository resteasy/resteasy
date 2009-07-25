package org.jboss.fastjaxb.tests;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocateDirectory
{
   public static String getTopDirectory()
   {
      String resourcePath = LocateDirectory.class.getName().replace('.', '/') + ".class";
      URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
      if (url == null) return null;
      String urlStr = url.getPath();
      urlStr = urlStr.substring(0, urlStr.lastIndexOf("test-fast-jaxb/target/test-classes/" + resourcePath));
      return urlStr;
   }
}
