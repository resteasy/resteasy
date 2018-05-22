package org.jboss.resteasy.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.PortProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Test utility class
 *
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class TestPortProvider
{
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
    * @param <T> type
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
    * @param <T> type
    * @param clazz the client interface class
    * @param path the base request path
    * @return the proxy object
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
