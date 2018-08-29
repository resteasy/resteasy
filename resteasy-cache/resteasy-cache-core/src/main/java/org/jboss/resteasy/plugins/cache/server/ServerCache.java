package org.jboss.resteasy.plugins.cache.server;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ServerCache
{
   interface Entry
   {
      int getExpirationInSeconds();

      boolean isExpired();

      String getEtag();

      byte[] getCached();

      MultivaluedMap<String, Object> getHeaders();
   }

   Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag);

   Entry get(String uri, MediaType accept);

   void remove(String uri);

   void clear();
}
