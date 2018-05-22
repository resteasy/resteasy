package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;

/**
 * Implement a client proxy for ProxyFactory. This class implements each method
 * using an EntityExtractor
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractor
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractorFactory
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.ResponseObjectEntityExtractorFactory
 */
@SuppressWarnings("unchecked")
public class ClientResponseProxy implements InvocationHandler
{
   private ClientContext context;
   private Map<Method, EntityExtractor<?>> methodMap;
   private Class<?> clazz;

   public ClientResponseProxy(ClientContext context, Map<Method, EntityExtractor<?>> methodMap, Class<?> clazz)
   {
      super();
      this.methodMap = methodMap;
      this.clazz = clazz;
      this.context = context;
   }

   public Object invoke(Object o, Method method, Object[] args) throws Throwable
   {
      // equals and hashCode were added for cases where the proxy is added to
      // collections. The Spring transaction management, for example, adds
      // transactional Resources to a Collection, and it calls equals and
      // hashCode.
      if (method.getName().equals("equals"))
      {
         return this.equals(o);
      }
      else if (method.getName().equals("hashCode"))
      {
         return this.hashCode();
      }

      EntityExtractor entityExtractor = methodMap.get(method);
      if (entityExtractor == null)
         throw new RuntimeException(Messages.MESSAGES.couldNotProcessMethod(method));

      return entityExtractor.extractEntity(context, entityExtractor, args);
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof ClientResponseProxy))
         return false;
      ClientResponseProxy other = (ClientResponseProxy) obj;
      if (other == this)
         return true;
      if (other.clazz != this.clazz)
         return false;
      return super.equals(obj);
   }

   @Override
   public int hashCode()
   {
      return clazz.hashCode();
   }

   public String toString()
   {
      return Messages.MESSAGES.clientProxyFor(clazz.getName());
   }
}
