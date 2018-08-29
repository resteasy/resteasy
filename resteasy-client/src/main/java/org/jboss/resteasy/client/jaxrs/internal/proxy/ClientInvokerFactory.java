package org.jboss.resteasy.client.jaxrs.internal.proxy;

import java.lang.reflect.Method;

import org.jboss.resteasy.client.jaxrs.ProxyConfig;

public interface ClientInvokerFactory
{
   ClientInvoker createClientInvoker(Class<?> clazz, Method method, ProxyConfig config);
}
