package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.util.PortProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;

/**
 * Test utility class
 *
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class TestPortProvider
{
   /**
    * Creates a ResteasyWebTarget using base request path.
    * @param path the path
    * @return web resource target
    */
   public static ResteasyWebTarget createTarget(String path)
   {
      return (ResteasyWebTarget) ClientBuilder.newClient().target(generateURL(path));
   }
   
   /**
    * Create a Resteasy client proxy with an empty base request path.
    *
    * @param <T> type
    * @param clazz the client interface class
    * @return the proxy object
    */
   public static <T> T createProxy(Class<T> clazz)
   {
      return createProxy(clazz, generateBaseUrl());
   }

   /**
    * Create a Resteasy client proxy.
    *
    * @param <T> type
    * @param clazz the client interface class
    * @param url request url
    * @return the proxy object
    */
   public static <T> T createProxy(Class<T> clazz, String url)
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ResteasyClientBuilder.newClient().target(url);
      return target.proxy(clazz);
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
    * @throws MalformedURLException if no protocol is specified or an unknown protocol is found 
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
      return PortProvider.getPort();
   }
   
   /**
    * Look up the configured hostname, first checking an environment variable (RESTEASY_HOST),
    * then a system property (org.jboss.resteasy.host), and finally the default hostname (localhost).
    *
    * @return the host specified in either the environment or system properties
    */
   public static String getHost()
   {
      return PortProvider.getHost();
   }
}
