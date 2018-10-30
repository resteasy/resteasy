package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;

public interface ApacheHttpClientEngine extends ClientHttpEngine
{
   /**
    * Enumeration to represent memory units.
    */
   enum MemoryUnit {
      /**
       * Bytes
       */
      BY,
      /**
       * Killo Bytes
       */
      KB,

      /**
       * Mega Bytes
       */
      MB,

      /**
       * Giga Bytes
       */
      GB
   }

   static ApacheHttpClientEngine create()
   {
      return new ApacheHttpClient43Engine();
   }

   static ApacheHttpClientEngine create(CloseableHttpClient httpClient)
   {
      return new ApacheHttpClient43Engine(httpClient);
   }

   static ApacheHttpClientEngine create(HttpClient httpClient, boolean closeHttpClient)
   {
      return new ApacheHttpClient43Engine(httpClient, closeHttpClient);
   }
}
