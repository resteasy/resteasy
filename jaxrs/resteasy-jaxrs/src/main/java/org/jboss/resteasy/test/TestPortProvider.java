/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ProxyFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Utility class that provides a port number for the Resteasy embedded container.
 *
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class TestPortProvider
{
   private static final int DEFAULT_PORT = 8081;

   private static final String ENV_VAR_NAME = "RESTEASY_PORT";

   private static final String PROPERTY_NAME = "org.jboss.resteasy.port";

   /**
    * Create a Resteasy ClientRequest object using the configured port.
    *
    * @param path the request path
    * @return the ClientRequest object
    */
   public static ClientRequest createClientRequest(String path)
   {
      return new ClientRequest(generateURL(path));
   }

   /**
    * Create a commons-httpclient DeleteMethod object.
    *
    * @param path the request path
    * @return a DeleteMethod object
    */
   public static DeleteMethod createDeleteMethod(String path)
   {
      return new DeleteMethod(generateURL(path));
   }

   /**
    * Create a commons-httpclient GetMethod object.
    *
    * @param path the request path
    * @return a GetMethod object
    */
   public static GetMethod createGetMethod(String path)
   {
      return new GetMethod(generateURL(path));
   }

   /**
    * Create a commons-httpclient OptionsMethod object.
    *
    * @param path the request path
    * @return an OptionsMethod object
    */
   public static OptionsMethod createOptionsMethod(String path)
   {
      return new OptionsMethod(generateURL(path));
   }

   /**
    * Create a commons-httpclient PostMethod object.
    *
    * @param path the request path
    * @return a PostMethod object
    */
   public static PostMethod createPostMethod(String path)
   {
      return new PostMethod(generateURL(path));
   }

   /**
    * Create a Resteasy client proxy with an empty base request path.
    *
    * @param clazz the client interface class
    * @return the proxy object
    */
   public static <T> T createProxy(Class<T> clazz)
   {
      return createProxy(clazz, "");
   }

   /**
    * Create a Resteasy client proxy.
    *
    * @param clazz the client interface class
    * @return the proxy object
    * @path the base request path
    */
   public static <T> T createProxy(Class<T> clazz, String path)
   {
      return ProxyFactory.create(clazz, generateURL(path));
   }

   /**
    * Create a commons-httpclient PutMethod object.
    *
    * @param path the request path
    * @return a PutMethod object
    */
   public static PutMethod createPutMethod(String path)
   {
      return new PutMethod(generateURL(path));
   }

   /**
    * Create a URI for the provided path, using the configured port
    *
    * @param path the request path
    * @return a full URI
    */
   public static URI createURI(String path)
   {
      return URI.create(generateURL(path));
   }

   /**
    * Create a URL for the provided path, using the configured port
    *
    * @param path the request path
    * @return a full URL
    */
   public static URL createURL(String path) throws MalformedURLException
   {
      return new URL(generateURL(path));
   }

   /**
    * Generate a base URL incorporating the configured port.
    *
    * @return a full URL
    */
   public static String generateBaseUrl()
   {
      return generateURL("");
   }

   /**
    * Generate a URL incorporating the configured port.
    *
    * @param path the path
    * @return a full URL
    */
   public static String generateURL(String path)
   {
      return String.format("http://localhost:%d%s", getPort(), path);
   }

   /**
    * Look up the configured port number, first checking an environment variable (RESTEASY_PORT),
    * then a system property (org.jboss.resteasy.port), and finally the default port (8081).
    *
    * @return the port number specified in either the environment or system properties
    */
   public static int getPort()
   {
      int port = -1;
      String property = System.getenv(ENV_VAR_NAME);
      if (property != null)
      {
         try
         {
            port = Integer.parseInt(property);
         }
         catch (NumberFormatException e)
         {
         }
      }

      if (port == -1)
      {
         property = System.getProperty(PROPERTY_NAME);
         if (property != null)
         {
            try
            {
               port = Integer.parseInt(property);
            }
            catch (NumberFormatException e)
            {
            }
         }
      }

      if (port == -1)
      {
         port = DEFAULT_PORT;
      }
      return port;
   }
}
