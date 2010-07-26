package org.jboss.resteasy.examples.oauth;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("receiver/standalone")
public class MessageReceiver
{
    private volatile String greetingMessage;
    
    @GET
    @RolesAllowed("JBossAdmin")
    public String getMessage()
    {
        return greetingMessage;
    }
    
    @POST
    @Consumes("text/plain")
    @RolesAllowed("user")
    public Response receiveMessage(String value)
    {
        greetingMessage = value;
        return Response.ok().build();
    }
}
