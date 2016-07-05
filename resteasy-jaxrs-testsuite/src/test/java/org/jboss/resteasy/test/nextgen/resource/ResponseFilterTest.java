package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseFilterTest extends BaseResourceTest
{
   @Path("resource")
   public static class Resource
   {


      @POST
      @Path("getstatus")
      public Response getStatus(String entity)
      {
         int status = Integer.parseInt(entity);
         Response.ResponseBuilder builder = Response.ok();
         Response response = builder.status(status).build();
         return response;
      }

      @POST
      @Path("getstatusinfo")
      public Response getStatusinfo(String entity)
      {
         return getStatus(entity);
      }

      @POST
      @Path("getentitytype")
      public Response getEntityType(String type) {
         Response.ResponseBuilder builder = Response.ok();
         Object entity = null;
         String content = "ENTity";
         if ("string".equals(type))
            entity = content;
         else if ("bytearray".equals(type))
            entity = content.getBytes();
         else if ("inputstream".equals(type))
            entity = new ByteArrayInputStream(content.getBytes());
         builder = builder.entity(entity);
         Response response = builder.build();
         return response;
      }

   }

   public static class ResponseFilter implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         if (requestContext.getUriInfo().getPath().endsWith("getstatus")) {
            int status = responseContext.getStatus();
            responseContext.setStatus(Response.Status.OK.getStatusCode());
            responseContext.setEntity(String.valueOf(status), null, MediaType.TEXT_PLAIN_TYPE);
         } else if (requestContext.getUriInfo().getPath().endsWith("getentitytype")) {
            Type type = responseContext.getEntityType();
            String name = "NULL";
            if (type instanceof Class)
               name = ((Class<?>) type).getName();
            else if (type != null)
               name = type.getClass().getName();
            responseContext.setEntity(name, null, MediaType.TEXT_PLAIN_TYPE);

         } else if (requestContext.getUriInfo().getPath().endsWith("getstatusinfo")) {
            Response.StatusType type = responseContext.getStatusInfo();
            if (type == null) {
               responseContext.setEntity("NULL", null, MediaType.TEXT_PLAIN_TYPE);
               responseContext.setStatus(Response.Status.OK.getStatusCode());
               return;
            }
            int status = type.getStatusCode();
            responseContext.setStatus(Response.Status.OK.getStatusCode());
            responseContext.setEntity(String.valueOf(status), null, MediaType.TEXT_PLAIN_TYPE);

         }
      }
   }


   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(ResponseFilter.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testStatus()
   {
      for (Response.Status status : Response.Status.values()) {
         String content = String.valueOf(status.getStatusCode());
         //System.out.println("code: " + content);
         Response response = client.target(generateURL("/resource/getstatus")).request().post(Entity.text(content));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(content, response.readEntity(String.class));
         response.close();
      }

   }

   @Test
   public void testStatusInfo()
   {
      for (Response.Status status : Response.Status.values()) {
         String content = String.valueOf(status.getStatusCode());
         //System.out.println("code: " + content);
         Response response = client.target(generateURL("/resource/getstatusinfo")).request().post(Entity.text(content));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(content, response.readEntity(String.class));
         response.close();
      }

   }


   @Test
   public void testEntityType()
   {
      String content = "string";
      //System.out.println("code: " + content);
      Response response = client.target(generateURL("/resource/getentitytype")).request().post(Entity.text(content));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(String.class.getName(), response.readEntity(String.class));
      response.close();

   }


}
