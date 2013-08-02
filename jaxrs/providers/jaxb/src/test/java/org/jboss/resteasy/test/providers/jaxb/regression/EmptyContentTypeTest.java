package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * RESTEASY-518, 529
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmptyContentTypeTest  extends BaseResourceTest
{
   @XmlRootElement
   public static class Foo
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

   }

   @Path("/test1")
   public static class Test1
   {
      @POST
      @Consumes(MediaType.APPLICATION_XML)
      public Response post(Foo foo)
      {
         return Response.ok(foo.getName(), "text/plain").build();
      }


      @POST
      public Response postNada(@HeaderParam("Content-Type") String contentType)
      {
         Assert.assertEquals(null, contentType);
         return Response.ok("NULL", "text/plain").build();
      }
   }


   @Path("/test2")
   public static class Test2
   {
      @POST
      public Response postNada(@HeaderParam("Content-Type") String contentType)
      {
         Assert.assertEquals(null, contentType);
         return Response.ok("NULL", "text/plain").build();
      }

      @POST
      @Consumes(MediaType.APPLICATION_XML)
      public Response post(Foo foo)
      {
         return Response.ok(foo.getName(), "text/plain").build();
      }


   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Test1.class);
      addPerRequestResource(Test2.class);
   }

   @Test
   public void test1() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test1"));
      Foo foo = new Foo();
      foo.setName("Bill");
      request.body("application/xml", foo);
      ClientResponse res = request.post();
      Assert.assertEquals(res.getEntity(String.class), "Bill");

      request = new ClientRequest(generateURL("/test1"));
      res = request.post();
      Assert.assertEquals(res.getEntity(String.class), "NULL");


   }

   @Test
   public void test2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test2"));
      Foo foo = new Foo();
      foo.setName("Bill");
      request.body("application/xml", foo);
      ClientResponse res = request.post();
      Assert.assertEquals(res.getEntity(String.class), "Bill");

      request = new ClientRequest(generateURL("/test2"));
      res = request.post();
      Assert.assertEquals(res.getEntity(String.class), "NULL");


   }

}
