package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.annotations.GZIP;

@Path("")
public class GZIPAnnotationResource implements GZIPAnnotationInterface
{
   @Context
   HttpHeaders headers;

   @Path("/foo")
   @Consumes("text/plain")
   @Produces("text/plain")
   @GZIP
   @POST
   @Override
   public String getFoo(String request) {

      if ("test".equals(request)) {
         String contentEncoding = headers.getRequestHeader(HttpHeaders.CONTENT_ENCODING).get(0);
         String acceptEncoding = headers.getRequestHeader(HttpHeaders.ACCEPT_ENCODING).get(0);
         return contentEncoding + "|" + acceptEncoding;
      } else {
         throw new RuntimeException("request != \"test\"");
      }
   }
}
