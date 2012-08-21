package org.jboss.resteasy.client.jaxrs;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyWebTarget extends WebTarget
{
   ResteasyClient getResteasyClient();

   <T> T proxy(Class<T> proxyInterface);

   <T> ProxyBuilder<T> proxyBuilder(Class<T> proxyInterface);

   // override


   @Override
   ResteasyWebTarget path(String path) throws NullPointerException;

   @Override
   ResteasyWebTarget pathParam(String name, Object value) throws IllegalArgumentException, NullPointerException;

   @Override
   ResteasyWebTarget pathParams(Map<String, Object> parameters) throws IllegalArgumentException, NullPointerException;

   @Override
   ResteasyWebTarget matrixParam(String name, Object... values) throws NullPointerException;

   @Override
   ResteasyWebTarget queryParam(String name, Object... values) throws NullPointerException;

   @Override
   ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException;

   ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException;

   ResteasyWebTarget path(Method method) throws IllegalArgumentException;

   ResteasyWebTarget clone();
}
