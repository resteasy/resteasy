package org.jboss.resteasy.test.client.exception.resource;

import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ClientExceptionsResource {
   @POST
   @Path("post")
   public Response post(ClientExceptionsData data) {
      return Response.ok().entity(data).build();
   }

   @GET
   @Path("get")
   public String get() {
      return "OK";
   }

   @Path("data")
   @GET
   public Response getData(@Context HttpHeaders headers) {
      Response response = Response.ok()
            .type(headers.getAcceptableMediaTypes().get(0))
            .entity(new ClientExceptionsData("test", "test"))
            .build();
      return response;
   }

   @Path("senddata")
   @POST
   public ClientExceptionsData postandget(ClientExceptionsData data) {
      return data;
   }

   @Path("empty")
   @GET
   public Response getEmpty(@Context HttpHeaders headers) {
      Response response = Response.ok()
            .type(headers.getAcceptableMediaTypes().get(0))
            .header(HttpHeaders.CONTENT_LENGTH, 0)
            .build();
      return response;
   }

   @GET
   @Path("error")
   public Response error() {
      throw new WebApplicationException(HttpResponseCodes.SC_FORBIDDEN);
   }
}
