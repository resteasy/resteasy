package org.jboss.resteasy.examples.oauth;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("receiver")
public class MessageReceiver
{
    private volatile String greetingMessage;
    
    @GET
    @Path("message")
    public String getMessage()
    {
        return greetingMessage;
    }
    
    @POST
    @Path("sink")
    @Consumes("text/plain")
    public Response receiveMessage(String value)
    {
        greetingMessage = value;
        return Response.ok().build();
    }
}
