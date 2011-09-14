package org.jboss.resteasy.client;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.client.core.MethodInvoker;
import org.jboss.resteasy.client.core.SubResourceInvoker;
import org.jboss.resteasy.client.core.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

public class ProxyBuilder<T>
{
	public static <T> ProxyBuilder<T> build(Class<T> iface, URI base)
	{
		return new ProxyBuilder<T>(iface, base);
	}
	
	public static <T> ProxyBuilder<T> build(Class<T> iface, String base)
	{
		return new ProxyBuilder<T>(iface, ProxyFactory.createUri(base));
	}
	
	private final Class<T> iface;
	private final URI baseUri;
	private ClassLoader loader;
	private ClientExecutor executor = ClientRequest.getDefaultExecutor();
	private ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
	private EntityExtractorFactory extractorFactory = new DefaultEntityExtractorFactory();
	private Map<String, Object> requestAttributes = Collections.emptyMap();
	
	private ProxyBuilder(Class<T> iface, URI base)
	{
		this.iface = iface;
		this.baseUri = base;
		this.loader = iface.getClassLoader();
	}
	
	public ProxyBuilder<T> classloader(ClassLoader cl)
	{
		this.loader = cl;
		return this;
	}
	
	public ProxyBuilder<T> executor(ClientExecutor exec)
	{
		this.executor = exec;
		return this;
	}
	
	public ProxyBuilder<T> providerFactory(ResteasyProviderFactory fact)
	{
		this.providerFactory = fact;
		return this;
	}
	
	public ProxyBuilder<T> extractorFactory(EntityExtractorFactory fact)
	{
		this.extractorFactory = fact;
		return this;
	}
	
	public ProxyBuilder<T> requestAttributes(Map<String, Object> attrs)
	{
		this.requestAttributes = attrs;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public T now()
	{
		HashMap<Method, MethodInvoker> methodMap = new HashMap<Method, MethodInvoker>();

		if (providerFactory instanceof ProviderFactoryDelegate)
		{
			providerFactory = ((ProviderFactoryDelegate) providerFactory).getDelegate();
		}

		for (Method method : iface.getMethods())
		{
			MethodInvoker invoker;
			Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
			if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface())
			{
				invoker = new SubResourceInvoker(baseUri, method, providerFactory, executor, extractorFactory, loader);
			}
			else
			{
				invoker = ProxyFactory.createClientInvoker(iface, method, baseUri, executor, providerFactory, extractorFactory, requestAttributes);
			}
			methodMap.put(method, invoker);
		}

		Class<?>[] intfs =
		{
				iface, ResteasyClientProxy.class
		};

		ClientProxy clientProxy = new ClientProxy(methodMap);
		// this is done so that equals and hashCode work ok. Adding the proxy to a
		// Collection will cause equals and hashCode to be invoked. The Spring
		// infrastructure had some problems without this.
		clientProxy.setClazz(iface);

		return (T) Proxy.newProxyInstance(loader, intfs, clientProxy);
	}
}
