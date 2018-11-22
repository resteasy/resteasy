package org.jboss.resteasy.test;

import io.netty.channel.ChannelHandlerContext;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
   @Path("empty")
   public void empty() {

   }

   @GET
   @Path("query")
   public String query(@QueryParam("param") String value) {
      return value;

   }


   @GET
   @Path("/exception")
   @Produces("text/plain")
   public String exception() {
      throw new RuntimeException();
   }

   @GET
   @Path("large")
   @Produces("text/plain")
   public String large() {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < 1000; i++) {
         buf.append(i);
      }
      return buf.toString();
   }

   @GET
   @Path("/context")
   @Produces("text/plain")
   public String context(@Context ChannelHandlerContext context) {
      return context.channel().toString();
   }

   @POST
   @Path("/post")
   @Produces("text/plain")
   public String post(String postBody) {
      return postBody;
   }

   @PUT
   @Path("/leak")
   public String put(String contents) {
      return contents;
   }

   @GET
   @Path("/test/absolute")
   @Produces("text/plain")
   public String absolute(@Context UriInfo info)
   {
      return "uri: " + info.getRequestUri().toString();
   }
}