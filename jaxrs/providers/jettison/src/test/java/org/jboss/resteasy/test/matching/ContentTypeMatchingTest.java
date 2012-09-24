package org.jboss.resteasy.test.matching;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * This tests automatically picking content type based on Accept header and/or @Produces
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContentTypeMatchingTest extends BaseResourceTest
{
   @XmlRootElement
   public static class Error
   {
      private String name = "foo";

      @XmlElement
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   public static class MyErrorException extends RuntimeException
   {

   }

   @Provider
   public static class MyErrorExceptinMapper implements ExceptionMapper<MyErrorException>
   {
      public Response toResponse(MyErrorException exception)
      {
         return Response.status(412).entity(new Error()).build();
      }
   }

   @Path("/mapper")
   public static class MapperResource
   {
      @Path("produces")
      @Produces("application/xml")
      @GET
      public String getProduces()
      {
         throw new MyErrorException();
      }

      @Path("accepts-produces")
      @Produces({"application/xml", "application/json"})
      @GET
      public String getAcceptsProduces()
      {
         throw new MyErrorException();
      }

      @Path("accepts")
      @GET
      public String getAccepts()
      {
         throw new MyErrorException();
      }

      @Path("accepts-entity")
      @GET
      public Error getEntity()
      {
         return new Error();
      }
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(MapperResource.class);
      deployment.getProviderFactory().registerProvider(MyErrorExceptinMapper.class);

   }

   @Test
   public void testProduces() throws Exception
   {
      // test that media type is chosen from resource method
      ClientRequest request = new ClientRequest(generateURL("/mapper/produces"));
      ClientResponse response = request.get();
      Assert.assertEquals(412, response.getStatus());
      Assert.assertEquals("application/xml", response.getResponseHeaders().getFirst("Content-Type"));
      String error = (String) response.getEntity(String.class);
      Assert.assertNotNull(error);
      System.out.println(error);

   }

   @Test
   public void testAcceptsProduces() throws Exception
   {
      // test that media type is chosen from resource method and accepts
      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts-produces"));
         request.accept("application/json");
         ClientResponse response = request.get();
         Assert.assertEquals(412, response.getStatus());
         Assert.assertEquals("application/json", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts-produces"));
         request.accept("application/xml");
         ClientResponse response = request.get();
         Assert.assertEquals(412, response.getStatus());
         Assert.assertEquals("application/xml", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }
   }

   @Test
   public void testAccepts() throws Exception
   {
      // test that media type is chosen from accepts
      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts"));
         request.accept("application/json");
         ClientResponse response = request.get();
         Assert.assertEquals(412, response.getStatus());
         Assert.assertEquals("application/json", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts"));
         request.accept("application/xml");
         ClientResponse response = request.get();
         Assert.assertEquals(412, response.getStatus());
         Assert.assertEquals("application/xml", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }
   }

   @Test
   public void testAcceptsEntity() throws Exception
   {
      // test that media type is chosen from accepts
      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts-entity"));
         request.accept("application/json");
         ClientResponse response = request.get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("application/json", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/mapper/accepts-entity"));
         request.accept("application/xml");
         ClientResponse response = request.get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("application/xml", response.getResponseHeaders().getFirst("Content-Type"));
         String error = (String) response.getEntity(String.class);
         Assert.assertNotNull(error);
         System.out.println(error);
      }
   }
}
