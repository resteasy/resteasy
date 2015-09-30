package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ProxyBuilder;
import org.jboss.resteasy.client.ProxyConfig;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientProxy implements InvocationHandler
{
	private Map<Method, MethodInvoker> methodMap;
	private Class<?> clazz;
	private final URI base;
	private final ProxyConfig config;

	public ClientProxy(Map<Method, MethodInvoker> methodMap, URI base, ProxyConfig config)
	{
		super();
		this.methodMap = methodMap;
		this.base = base;
		this.config = config;
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

      MethodInvoker clientInvoker = methodMap.get(method);
      if (clientInvoker == null)
      {
         if (method.getName().equals("equals"))
         {
            return this.equals(o);
         }
         else if (method.getName().equals("hashCode"))
         {
            return this.hashCode();
         }
         else if (method.getName().equals("toString") && (args == null || args.length == 0))
         {
            return this.toString();
         }
         else if (method.getName().equals("getResteasyClientInvokers"))
         {
            return methodMap.values();
         }
         else if (method.getName().equals("applyClientInvokerModifier"))
         {
            ClientInvokerModifier modifier = (ClientInvokerModifier) args[0];
            for (MethodInvoker invoker : methodMap.values())
            {
            	if(invoker instanceof ClientInvoker)
               		modifier.modify((ClientInvoker)invoker);
            }

            return null;
         }
         else if(method.getName().equals("as") && args.length == 1 && args[0] instanceof Class)
         {
        	 return ProxyBuilder.createProxy((Class<?>)args[0], base, config);
         }
      }

      if (clientInvoker == null)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotFindMethod(method));
      }
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
