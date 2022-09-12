package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

@Path("resource")
public class ResponseFilterResource {

   @POST
   @Path("getstatus")
   public Response getStatus(String entity) {
      int status = Integer.parseInt(entity);
      Response.ResponseBuilder builder = Response.ok();
      Response response = builder.status(status).build();
      return response;
   }

   @POST
   @Path("getstatusinfo")
   public Response getStatusinfo(String entity) {
      return getStatus(entity);
   }

   @POST
   @Path("getentitytype")
   public Response getEntityType(String type) {
      Response.ResponseBuilder builder = Response.ok();
      Object entity = null;
      String content = "ENTity";
      if ("string".equals(type)) {
         entity = content;
      } else if ("bytearray".equals(type)) {
         entity = content.getBytes();
      } else if ("inputstream".equals(type)) {
         entity = new ByteArrayInputStream(content.getBytes());
      }
      builder = builder.entity(entity);
      Response response = builder.build();
      return response;
   }

}
