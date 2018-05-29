package org.jboss.resteasy.client;

import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @deprecated The Resteasy proxy facility in the client framework in resteasy-jaxrs is replaced by the
 * proxy facility extension in the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 * @see org.jboss.resteasy.client.ProxyConfig
 */
@Deprecated
public class ProxyConfig
{
	private final ClassLoader loader;
	private final ClientExecutor executor;
	private final ResteasyProviderFactory providerFactory;
	private final EntityExtractorFactory extractorFactory;
	private final Map<String, Object> requestAttributes;
	private final MediaType serverConsumes;
	private final MediaType serverProduces;

	public ProxyConfig(ClassLoader loader, ClientExecutor executor, ResteasyProviderFactory providerFactory, EntityExtractorFactory extractorFactory, Map<String, Object> requestAttributes, MediaType serverConsumes, MediaType serverProduces)
	{
		super();
		this.loader = loader;
		this.executor = executor;
		this.providerFactory = providerFactory;
		this.extractorFactory = extractorFactory;
		this.requestAttributes = requestAttributes;
		this.serverConsumes = serverConsumes;
		this.serverProduces = serverProduces;
	}

	public ClassLoader getLoader()
	{
		return loader;
	}

	public ClientExecutor getExecutor()
	{
		return executor;
	}

	public ResteasyProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	public EntityExtractorFactory getExtractorFactory()
	{
		return extractorFactory;
	}

	public Map<String, Object> getRequestAttributes()
	{
		return requestAttributes;
	}

	public MediaType getServerConsumes()
	{
		return serverConsumes;
	}

	public MediaType getServerProduces()
	{
		return serverProduces;
	}
}
