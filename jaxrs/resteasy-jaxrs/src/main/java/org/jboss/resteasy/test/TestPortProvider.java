/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
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

   private static final String DEFAULT_HOST = "localhost";

   private static final String ENV_VAR_HOSTNAME = "RESTEASY_HOST";

   private static final String PROPERTY_HOSTNAME = "org.jboss.resteasy.host";
   
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

   public static ClientRequest createClientRequest(ClientRequestFactory factory, String path)
   {
      return factory.createRequest(generateURL(path));
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
      return String.format("http://%s:%d%s", getHost(), getPort(), path);
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
   
   /**
    * Look up the configured hostname, first checking an environment variable (RESTEASY_HOST),
    * then a system property (org.jboss.resteasy.host), and finally the default hostname (localhost).
    *
    * @return the host specified in either the environment or system properties
    */
   public static String getHost()
   {
      String host = null;
      String property = System.getenv(ENV_VAR_HOSTNAME);
      if (property != null)
      {
         host = property;
      }

      if (host == null)
      {
         property = System.getProperty(PROPERTY_HOSTNAME);
         if (property != null)
         {
            host = property;
         }
      }

      if (host == null)
      {
         host = DEFAULT_HOST;
      }
      return host;
   }
}
