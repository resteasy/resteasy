package org.jboss.resteasy.test.providers.jaxb.seealso;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;

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
         Assert.assertTrue(foo instanceof RealFoo);
         Assert.assertEquals(((RealFoo) foo).getName(), "bill");
         return foo;
      }


      @Path("intf")
      @POST
      @Produces(MediaType.APPLICATION_XML)
      @Consumes(MediaType.APPLICATION_XML)
      public IFoo post(IFoo foo)
      {
         Assert.assertTrue(foo instanceof RealFoo);
         Assert.assertEquals(((RealFoo) foo).getName(), "bill");
         return foo;
      }
   }


   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(SeeAlso.class);
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

   private void runTest(String url)
           throws JAXBException, IOException
   {
      HttpClient client = new HttpClient();

      JAXBContext ctx = JAXBContext.newInstance(RealFoo.class);
      StringWriter writer = new StringWriter();
      RealFoo foo = new RealFoo();
      foo.setName("bill");

      ctx.createMarshaller().marshal(foo, writer);

      String s = writer.getBuffer().toString();
      System.out.println(s);

      PostMethod method = new PostMethod(url);
      method.addRequestHeader("Content-Type", "application/xml");
      method.setRequestBody(s);
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      foo = (RealFoo) ctx.createUnmarshaller().unmarshal(method.getResponseBodyAsStream());
      Assert.assertEquals(((RealFoo) foo).getName(), "bill");
   }

}
