package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ClientResponseProxy implements InvocationHandler
{
   private ClientRequest request;
   private BaseClientResponse response;
   private Map<Method, ResponseHandler> methodMap;
   private Class<?> clazz;

   public ClientResponseProxy(ClientRequest request, BaseClientResponse response, Map<Method, ResponseHandler> methodMap,
         Class<?> clazz)
   {
      super();
      this.request = request;
      this.response = response;
      this.methodMap = methodMap;
      this.clazz = clazz;
   }

   public Object invoke(Object o, Method method, Object[] args)
         throws Throwable
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

      ResponseHandler handler = methodMap.get(method);
      return handler.getResponseObject(request, response, args);
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
      return "Client Proxy for :" + clazz.getName();
   }
}
