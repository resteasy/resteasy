package org.jboss.resteasy.test.providers.jaxb;

import static org.jboss.resteasy.test.TestPortProvider.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.jaxb.Stylesheet;
import org.jboss.resteasy.annotations.providers.jaxb.XmlHeader;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.providers.jaxb.XmlHeaderTestClasses.Thing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This tests decorators in general as well as the @XmlHeader and @Stylesheet annotations
 */
public class XmlHeaderTest extends BaseResourceTest
{

   @Path("/test")
   public static class TestService
   {

      @GET
      @Path("/header")
      @Produces("application/xml")
      @XmlHeader("<?xml-stylesheet type='text/xsl' href='${baseuri}foo.xsl' ?>")
      public Thing get()
      {
         Thing thing = new Thing();
         thing.setName("bill");
         return thing;
      }

      @GET
      @Path("/stylesheet")
      @Produces("application/xml")
      @Stylesheet(type = "text/css", href = "${basepath}foo.xsl")
    //FIXME  @Junk
      public Thing getStyle()
      {
         Thing thing = new Thing();
         thing.setName("bill");
         return thing;
      }
   }

   @Override
   @Before
   public void before() throws Exception
   {
      addPerRequestResource(TestService.class, /* FIXME Junk.class, Junk2.class, MyDecorator.class, MyDecorator2.class, */Thing.class, XmlHeaderTestClasses.class);
      super.before();
   }

   @Test
   public void testHeader() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test/header"));
      String response = request.getTarget(String.class);
      System.out.println(response);
      Assert.assertTrue(response.contains("<?xml-stylesheet"));

   }

   @Test
   public void testStylesheet() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test/stylesheet"));
      String response = request.getTarget(String.class);
      System.out.println(response);
      Assert.assertTrue(response.contains("<?xml-stylesheet"));

   }

}
