package org.jboss.resteasy.client.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ProxyBuilder;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class SubResourceInvoker implements MethodInvoker
{
	final ResteasyProviderFactory providerFactory;
	final ClientExecutor executor;
	final EntityExtractorFactory extractorFactory;
	final Class<?> iface;
	final String base;
	final String format;

	public SubResourceInvoker(URI uri, Method method, ResteasyProviderFactory providerFactory, ClientExecutor executor, EntityExtractorFactory extractorFactory)
	{
		String base = uri.toString();
		if (!base.endsWith("/"))
			base = base + "/";
		this.base = base;
		this.iface = method.getReturnType();
		this.providerFactory = providerFactory;
		this.executor = executor;
		this.extractorFactory = extractorFactory;
		String path = method.getAnnotation(Path.class).value();
		if (path.startsWith("/"))
			path = path.substring(1);
		Annotation[][] params = method.getParameterAnnotations();
		int index = 1;
		for (Annotation[] param : params)
		{
			for (Annotation a : param)
			{
				if (a instanceof PathParam)
				{
					String name = ((PathParam) a).value();
					path = path.replace("{" + name + "}", "%" + index + "$s");
					break;
				}
			}
			index++;
		}
		this.format = path;
	}

	@Override
	public Object invoke(Object[] args)
	{
		String path = String.format(format, args);
		return ProxyBuilder.build(iface, ProxyFactory.createUri(base + path)).executor(executor).providerFactory(providerFactory).extractorFactory(extractorFactory).classloader(getClass().getClassLoader()).now();
	}
}
