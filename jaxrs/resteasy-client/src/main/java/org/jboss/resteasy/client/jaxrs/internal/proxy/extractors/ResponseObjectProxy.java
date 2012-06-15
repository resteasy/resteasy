package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * This class represents the proxying functionality for creating a
 * "rich response object" that has the @ResponseObject annotation. The method
 * implementations ware created in ResponseObjectEntityExtractorFactory
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractor , ResponseObjectEntityExtractorFactory
 */

@SuppressWarnings("unchecked")
public class ResponseObjectProxy<T> implements EntityExtractor
{
   private Class<T> returnType;
   private HashMap<Method, EntityExtractor<?>> methodHandlers;

   public ResponseObjectProxy(Method method, EntityExtractorFactory extractorFactory)
   {
      this.returnType = (Class<T>) method.getReturnType();
      this.methodHandlers = new HashMap<Method, EntityExtractor<?>>();
      for (Method interfaceMethod : this.returnType.getMethods())
      {
         this.methodHandlers.put(interfaceMethod, extractorFactory.createExtractor(interfaceMethod));
      }
   }

   public Object extractEntity(ClientContext context, Object... args)
   {
      Class<?>[] intfs = {returnType};
      ClientResponseProxy clientProxy = new ClientResponseProxy(context, methodHandlers, returnType);
      return Proxy.newProxyInstance(returnType.getClassLoader(), intfs, clientProxy);
   }

}
