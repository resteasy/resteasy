package org.jboss.resteasy.client.jaxrs;

import javax.ws.rs.core.MediaType;

public class ProxyConfig
{
	private final ClassLoader loader;
	private final MediaType defaultConsumes;
	private final MediaType defaultProduces;

	public ProxyConfig(ClassLoader loader, MediaType defaultConsumes, MediaType defaultProduces)
	{
		super();
		this.loader = loader;
		this.defaultConsumes = defaultConsumes;
		this.defaultProduces = defaultProduces;
	}

	public ClassLoader getLoader()
	{
		return loader;
	}

	public MediaType getDefaultConsumes()
	{
		return defaultConsumes;
	}

	public MediaType getDefaultProduces()
	{
		return defaultProduces;
	}
}
