package org.jboss.resteasy.test.providers.jaxb.seealso;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SeeAlsoTest extends BaseResourceTest
{
   @Path("/see")
   public static class SeeAlso
   {
      @Path("base")
      @POST
      @Produces(MediaType.APPLICATION_XML)
      @Consumes(MediaType.APPLICATION_XML)
      public BaseFoo post(BaseFoo foo)
      {
         assert foo instanceof RealFoo;
         assert "bill".equals(((RealFoo) foo).getName());
         return foo;
      }


      @Path("intf")
      @POST
      @Produces(MediaType.APPLICATION_XML)
      @Consumes(MediaType.APPLICATION_XML)
      public IFoo post(IFoo foo)
      {
        assert foo instanceof RealFoo;
        assert "bill".equals(((RealFoo) foo).getName());
         return foo;
      }
   }


   @Before
   public void setUp() throws Exception
   {
      stopContainer();
      createContainer(initParams, contextParams);
      addPerRequestResource(SeeAlso.class, RealFoo.class, IFoo.class, BaseFoo.class, SeeAlsoTest.class, BaseResourceTest.class);
      startContainer();
   }


   @Test
   public void testBase() throws Exception
   {
      String url = generateURL("/see/base");
      runTest(url);

   }

   @Test
   public void testIntf() throws Exception
   {
      String url = generateURL("/see/intf");
      runTest(url);

   }

   private void runTest(String url) throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(RealFoo.class);
      StringWriter writer = new StringWriter();
      RealFoo foo = new RealFoo();
      foo.setName("bill");

      ctx.createMarshaller().marshal(foo, writer);

      String s = writer.getBuffer().toString();
      System.out.println(s);

      ClientRequest request = new ClientRequest(url);
      request.header("Content-Type", "application/xml");
      request.body("application/xml", s);
      ClientResponse<InputStream> response = request.post(InputStream.class);
      Assert.assertEquals(200, response.getStatus());
      foo = (RealFoo) ctx.createUnmarshaller().unmarshal(response.getEntity());
      Assert.assertEquals(foo.getName(), "bill");
      response.releaseConnection();
   }

}
