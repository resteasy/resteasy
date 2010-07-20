package org.jboss.resteasy.examples.oauth;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("server")
public class ServiceProviderResource
{
    @RolesAllowed({"user","JBossAdmin"})
    @Path("/resource1")
    @GET
    public String getProtectedResource()
    {
        return "Resource1";
    }
    
    @RolesAllowed("JBossAdmin")
    @Path("/resource2")
    @GET
    public String getProtectedResource2()
    {
        return "Resource2";
    }
    
    @RolesAllowed("private")
    @Path("/invisible")
    @GET
    public String getProtectedResource3()
    {
        throw new RuntimeException();
    }
}
