package org.jboss.resteasy.client.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientProxy implements InvocationHandler
{
   private Map<Method, ClientInvoker> methodMap;
   private Class<?> clazz;

   public ClientProxy(Map<Method, ClientInvoker> methodMap)
   {
      this.methodMap = methodMap;
   }

   public Class<?> getClazz()
   {
      return clazz;
   }

   public void setClazz(Class<?> clazz)
   {
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
      else if (method.getName().equals("getResteasyClientInvokers"))
      {
         return methodMap.values();
      }
      ClientInvoker clientInvoker = methodMap.get(method);
      return clientInvoker.invoke(args);
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof ClientProxy))
         return false;
      ClientProxy other = (ClientProxy) obj;
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
