package org.jboss.resteasy.skeleton.key.as7.config;

import org.apache.catalina.Context;
import org.jboss.resteasy.skeleton.key.config.ManagedResourceConfigLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CatalinaManagedResourceConfigLoader extends ManagedResourceConfigLoader
{

   public CatalinaManagedResourceConfigLoader(Context context)
   {
      InputStream is = null;
      String path = context.getServletContext().getInitParameter("skeleton.key.config.file");
      if (path == null)
      {
         is = context.getServletContext().getResourceAsStream("/WEB-INF/resteasy-oauth.json");
      }
      else
      {
         try
         {
            is = new FileInputStream(path);
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }
      init(is);
   }

}