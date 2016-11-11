package org.jboss.resteasy.plugins.cache.server;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ServerCache
{
    public static boolean mayVary(Entry cached, MultivaluedMap<String, String> current) {
        boolean mayVary = false;
        for (Map.Entry<String, List<String>> entry : cached.getVaryHeaders().entrySet()) {
            String headerName = entry.getKey();
            mayVary |= !(current.containsKey(headerName) && current.get(headerName).containsAll(entry.getValue()));
        }
        return mayVary;
    }

   public static interface Entry
   {
      int getExpirationInSeconds();

      boolean isExpired();

      String getEtag();

      byte[] getCached();

      MultivaluedMap<String, Object> getHeaders();

      MultivaluedMap<String, String> getVaryHeaders();
   }

   Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag, MultivaluedMap<String, String> varyHeaders);

   Entry get(String uri, MediaType accept, MultivaluedMap<String, String> headers);

   void remove(String uri);

   void clear();
}
