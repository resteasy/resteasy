package org.jboss.resteasy.client;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * <p>
 * <b>Note.</b> org.jboss.resteasy.client.ClientResponse{@literal <}T{@literal >} is a generic type in the Resteasy client framework,
 * but org.jboss.resteasy.client.jaxrs.internal.ClientResponse in the resteasy-client module is not, so
 * EntityTypeFactory is no longer useful.
 * 
 */
@Deprecated
public interface EntityTypeFactory
{
   @SuppressWarnings("unchecked")
   Class getEntityType(int status, MultivaluedMap<String, Object> metadata);
}
