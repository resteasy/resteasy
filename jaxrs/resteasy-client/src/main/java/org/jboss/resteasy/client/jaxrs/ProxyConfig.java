package org.jboss.resteasy.client.jaxrs;

import javax.ws.rs.core.MediaType;

public class ProxyConfig
{
	private final ClassLoader loader;
	private final MediaType defaultConsumes;
	private final MediaType defaultProduces;
	private final MediaType preferredProduces;

	public ProxyConfig(ClassLoader loader, MediaType defaultConsumes, MediaType defaultProduces, MediaType preferredProduces)
	{
		super();
		this.loader = loader;
		this.defaultConsumes = defaultConsumes;
		this.defaultProduces = defaultProduces;
		this.preferredProduces = preferredProduces;
	}

	public ProxyConfig(ClassLoader loader, MediaType defaultConsumes, MediaType defaultProduces) {
		this(loader, defaultConsumes, defaultProduces, null);
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

	public MediaType getPreferredProduces()
	{
		return preferredProduces;
	}
}
