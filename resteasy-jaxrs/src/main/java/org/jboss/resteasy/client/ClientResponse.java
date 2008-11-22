package org.jboss.resteasy.client;

import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;

/**
 * Response interface for the RESTEasy client framework.  Use this in your client proxy interface method return type
 * declarations if you want access to the response entity as well as status and header information.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientResponse<T>
{
   T getEntity();

   MultivaluedMap<String, String> getHeaders();

   int getStatus();

   /**
    * Extract the response body with the provided type information
    *
    * @param type
    * @param genericType
    * @param <T2>
    * @return
    */
   <T2> T2 getEntity(Class<T2> type, Type genericType);

   /**
    * Extract the response body with the provided type information.  GenericType is a trick used to
    * pass in generic type information to the resteasy runtime.
    * <p/>
    * For example:
    * <pre>
    * List<String> list = response.getBody(new GenericType<List<String>() {});
    *
    * @param type
    * @param <T2>
    * @return
    */
   <T2> T2 getBody(GenericType<T2> type);

}
