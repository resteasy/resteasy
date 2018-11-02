package org.jboss.resteasy.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.jboss.netty.channel.ChannelHandlerContext;

@Path("/")
public class Resource
{
   @GET
   @Path("/test")
   @Produces("text/plain")
   public String hello()
   {
      return "hello world";
   }

   @GET
   @Path("/exception")
   @Produces("text/plain")
   public String exception() {
      throw new RuntimeException();
   }

   @GET
   @Path("/context")
   @Produces("text/plain")
   public String context(@Context ChannelHandlerContext context) {
      return context.getChannel().toString();
   }
}
