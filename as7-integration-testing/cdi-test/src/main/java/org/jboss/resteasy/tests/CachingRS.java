package org.jboss.resteasy.tests;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Path("caching")
@Singleton
public class CachingRS
{
   @Context
   Request request;

   @Context
   HttpHeaders headers;


   @Context
   HttpServletRequest httpRequest;

   @Context
   UriInfo uriInfo;

   private static AtomicInteger counter= new AtomicInteger(0);

   private int id = counter.incrementAndGet();

   private static Logger log = Logger.getLogger(CachingRS.class);

   @GET
   @Path("conditional")
   @Produces(MediaType.APPLICATION_XML)
   @Formatted
   public Response getConditional()
   {
      System.out.println("*****************************************************");
      System.out.println("Service id: " + id);
      System.out.println("called: " + " " + request.getMethod() + " " + uriInfo.getRequestUri());

      for (String key : headers.getRequestHeaders().keySet())
      {
         List<String> value = headers.getRequestHeader(key);
         System.out.println("jaxrs.header " + key + " = " + value);
      }

      for (Enumeration<String> e = httpRequest.getHeaderNames(); e.hasMoreElements(); )
      {
         String key = e.nextElement();
         String value = httpRequest.getHeader(key);
         System.out.println("httpServletRequest.header " + key + " = " + value);
      }
      return Response.ok("<xml/>").build();
   }
}