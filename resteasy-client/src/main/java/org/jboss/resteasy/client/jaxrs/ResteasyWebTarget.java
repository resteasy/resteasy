package org.jboss.resteasy.client.jaxrs;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
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
   ResteasyWebTarget resolveTemplate(String name, Object value) throws NullPointerException;

   @Override
   ResteasyWebTarget resolveTemplates(Map<String, Object> templateValues) throws NullPointerException;

   @Override
   ResteasyWebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) throws NullPointerException;

   @Override
   ResteasyWebTarget resolveTemplateFromEncoded(String name, Object value) throws NullPointerException;

   @Override
   ResteasyWebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) throws NullPointerException;

   @Override
   ResteasyWebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws NullPointerException;

   @Override
   ResteasyWebTarget path(String path) throws NullPointerException;

   @Override
   ResteasyWebTarget matrixParam(String name, Object... values) throws NullPointerException;

   @Override
   ResteasyWebTarget queryParam(String name, Object... values) throws NullPointerException;

   ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException;

   /**
    * Will encode any '{}' characters and not treat them as template parameters
    */
   ResteasyWebTarget queryParamNoTemplate(String name, Object... values) throws NullPointerException;

   /**
    * Will encode any '{}' characters and not treat them as template parameters
    */
   ResteasyWebTarget queryParamsNoTemplate(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException;

   ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException;

   ResteasyWebTarget path(Method method) throws IllegalArgumentException;

   ResteasyWebTarget clone();

   @Override
   ResteasyWebTarget property(String name, Object value);

   @Override
   ResteasyWebTarget register(Class<?> componentClass);

   @Override
   ResteasyWebTarget register(Class<?> componentClass, int priority);

   @Override
   ResteasyWebTarget register(Class<?> componentClass, Class<?>... contracts);

   @Override
   ResteasyWebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts);

   @Override
   ResteasyWebTarget register(Object component);

   @Override
   ResteasyWebTarget register(Object component, int priority);

   @Override
   ResteasyWebTarget register(Object component, Class<?>... contracts);

   @Override
   ResteasyWebTarget register(Object component, Map<Class<?>, Integer> contracts);
   
   ResteasyWebTarget setChunked(boolean chunked);

}
