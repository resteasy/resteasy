package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.PreMatchingStereotype;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;

@Provider
@PreMatchingStereotype
public class PreMatchingStereotypeProvider implements ContainerRequestFilter
{
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException
    {
        containerRequestContext.setRequestUri(URI.create(containerRequestContext.getUriInfo().getBaseUri() + "/stereotype/get"));
    }
}
