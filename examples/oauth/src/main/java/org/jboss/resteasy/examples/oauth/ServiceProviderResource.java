package org.jboss.resteasy.examples.oauth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("server")
public class ServiceProviderResource
{
    @Path("/resource1")
    @GET
    public String getProtectedResource()
    {
        return "Resource1";
    }
}
