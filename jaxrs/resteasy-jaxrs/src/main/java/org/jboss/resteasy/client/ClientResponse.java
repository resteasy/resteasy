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
   MultivaluedMap<String, String> getHeaders();

   int getStatus();

   /**
    * Unmarshal the target entity from the response OutputStream.  You must have type information set via <T>
    * otherwise, this will not work.
    * <p/>
    * This method actually does the reading on the OutputStream.  It will only do the read once.  Afterwards, it will
    * cache the result and return the cached result.
    *
    * @return
    */
   T getEntity();

   /**
    * Extract the response body with the provided type information
    * <p/>
    * This method actually does the reading on the OutputStream.  It will only do the read once.  Afterwards, it will
    * cache the result and return the cached result.
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
    * <p/>
    * <p/>
    * This method actually does the reading on the OutputStream.  It will only do the read once.  Afterwards, it will
    * cache the result and return the cached result.
    *
    * @param type
    * @param <T2>
    * @return
    */
   <T2> T2 getEntity(GenericType<T2> type);

}
