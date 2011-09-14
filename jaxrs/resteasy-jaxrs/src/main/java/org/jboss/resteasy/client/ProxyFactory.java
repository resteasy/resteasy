package org.jboss.resteasy.client;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyFactory
{

	public static <T> T create(Class<T> clazz, String base)
	{
		return create(clazz, base, ClientRequest.getDefaultExecutor());
	}

	public static <T> T create(Class<T> clazz, String base, Map<String, Object> requestAttributes)
	{
		return create(clazz, createUri(base), ClientRequest.getDefaultExecutor(), ResteasyProviderFactory.getInstance(), new DefaultEntityExtractorFactory(), requestAttributes);
	}

	public static <T> T create(Class<T> clazz, String base, ResteasyProviderFactory providerFactory, Map<String, Object> requestAttributes)
	{
		return create(clazz, createUri(base), ClientRequest.getDefaultExecutor(), providerFactory, new DefaultEntityExtractorFactory(), requestAttributes);
	}

	public static <T> T create(Class<T> clazz, String base, ClientExecutor client)
	{
		return create(clazz, createUri(base), client, ResteasyProviderFactory.getInstance());
	}

	public static URI createUri(String base)
	{
		try
		{
			return new URI(base);
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T create(Class<T> clazz, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory)
	{
		return create(clazz, baseUri, executor, providerFactory, new DefaultEntityExtractorFactory());
	}

	public static <T> T create(Class<T> clazz, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory, EntityExtractorFactory extractorFactory)
	{
		return create(clazz, baseUri, executor, providerFactory, extractorFactory, new HashMap<String, Object>());
	}

	public static <T> T create(Class<T> clazz, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory, EntityExtractorFactory extractorFactory, Map<String, Object> requestAttributes)
	{
		return ProxyBuilder.build(clazz, baseUri).executor(executor).providerFactory(providerFactory).extractorFactory(extractorFactory).requestAttributes(requestAttributes).now();
	}

	public static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory, EntityExtractorFactory extractorFactory)
	{
		return createClientInvoker(clazz, method, baseUri, executor, providerFactory, extractorFactory, new HashMap<String, Object>());
	}

	public static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory, EntityExtractorFactory extractorFactory, Map<String, Object> requestAttributes)
	{
		Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
		if (httpMethods == null || httpMethods.size() != 1)
		{
			throw new RuntimeException("You must use at least one, but no more than one http method annotation on: " + method.toString());
		}
		ClientInvoker invoker = new ClientInvoker(baseUri, clazz, method, providerFactory, executor, extractorFactory);
		invoker.getAttributes().putAll(requestAttributes);
		ClientInvokerInterceptorFactory.applyDefaultInterceptors(invoker, providerFactory, clazz, method);
		invoker.setHttpMethod(httpMethods.iterator().next());
		return invoker;
	}
}
