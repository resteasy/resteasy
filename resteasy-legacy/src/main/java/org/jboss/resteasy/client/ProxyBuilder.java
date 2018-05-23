package org.jboss.resteasy.client;

import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.client.core.MethodInvoker;
import org.jboss.resteasy.client.core.SubResourceInvoker;
import org.jboss.resteasy.client.core.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T> type
 * @deprecated The Resteasy proxy facility in the client framework in resteasy-jaxrs is replaced by the
 * proxy facility extension in the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 * @see org.jboss.resteasy.client.jaxrs.ProxyBuilder
 * @see org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
 */
@Deprecated
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
	private ClientExecutor executor;
	private ResteasyProviderFactory providerFactory;
	private EntityExtractorFactory extractorFactory;
	private Map<String, Object> requestAttributes;
	private MediaType serverConsumes;
	private MediaType serverProduces;

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

	/** Shortcut for serverProduces(type).serverConsumes(type).
	 * @param type media type
	 * @return proxy builder
	 */
	public ProxyBuilder<T> serverMediaType(MediaType type)
	{
		this.serverProduces = type;
		this.serverConsumes = type;
		return this;
	}

	public ProxyBuilder<T> serverProduces(MediaType type)
	{
		this.serverProduces = type;
		return this;
	}

	public ProxyBuilder<T> serverConsumes(MediaType type)
	{
		this.serverConsumes = type;
		return this;
	}

	public ProxyBuilder<T> requestAttributes(Map<String, Object> attrs)
	{
		this.requestAttributes = attrs;
		return this;
	}

	private static final Class<?>[] cClassArgArray =
	{
		Class.class
	};

	public T now()
	{
		if (providerFactory instanceof ProviderFactoryDelegate)
			providerFactory = ((ProviderFactoryDelegate) providerFactory).getDelegate();

		if (executor == null)
			executor = ClientRequest.getDefaultExecutor();
		if (providerFactory == null)
			providerFactory = ResteasyProviderFactory.getInstance();
		if (extractorFactory == null)
			extractorFactory = new DefaultEntityExtractorFactory();
		if (requestAttributes == null)
			requestAttributes = Collections.emptyMap();
		
		final ProxyConfig config = new ProxyConfig(loader, executor, providerFactory, extractorFactory, requestAttributes, serverConsumes, serverProduces);
		return createProxy(iface, baseUri, config);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(final Class<T> iface, URI baseUri, final ProxyConfig config)
	{
		HashMap<Method, MethodInvoker> methodMap = new HashMap<Method, MethodInvoker>();
		for (Method method : iface.getMethods())
		{
			// ignore the as method to allow declaration in client interfaces
			if (!("as".equals(method.getName()) && Arrays.equals(method.getParameterTypes(), cClassArgArray)))
			{
				MethodInvoker invoker;
				Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
				if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface())
				{
					invoker = new SubResourceInvoker(baseUri, method, config);
				}
				else
				{
					invoker = ProxyFactory.createClientInvoker(iface, method, baseUri, config);
				}
				methodMap.put(method, invoker);
			}
		}

		Class<?>[] intfs =
		{
				iface, ResteasyClientProxy.class
		};

		ClientProxy clientProxy = new ClientProxy(methodMap, baseUri, config);
		// this is done so that equals and hashCode work ok. Adding the proxy to a
		// Collection will cause equals and hashCode to be invoked. The Spring
		// infrastructure had some problems without this.
		clientProxy.setClazz(iface);

		return (T) Proxy.newProxyInstance(config.getLoader(), intfs, clientProxy);
	}
}
