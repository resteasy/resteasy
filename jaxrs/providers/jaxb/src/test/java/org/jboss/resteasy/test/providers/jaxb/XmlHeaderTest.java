package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.annotations.providers.jaxb.Stylesheet;
import org.jboss.resteasy.annotations.providers.jaxb.XmlHeader;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * This tests decorators in general as well as the @XmlHeader and @Stylesheet annotations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class XmlHeaderTest extends BaseResourceTest
{
   /**
    * Test correct type (Marshaller), but incorrect media type
    */
   @Decorator(processor = MyDecorator.class, target = Marshaller.class)
   public static @interface Junk
   {
   }

   @DecorateTypes("application/json")
   public static class MyDecorator implements DecoratorProcessor<Marshaller, Junk>
   {
      public Marshaller decorate(Marshaller target, Junk annotation, Class type, Annotation[] annotations, MediaType mediaType)
      {
         throw new RuntimeException("FAILURE!!!!");
      }
   }

   /**
    * Test correct media type, but incorrect type
    */
   @Decorator(processor = MyDecorator.class, target = Assert.class)
   public static @interface Junk2
   {
   }

   @DecorateTypes("application/xml")
   public static class MyDecorator2 implements DecoratorProcessor<Assert, Junk2>
   {
      public Assert decorate(Assert target, Junk2 annotation, Class type, Annotation[] annotations, MediaType mediaType)
      {
         throw new RuntimeException("FAILURE!!!!");
      }
   }

   @XmlRootElement
   @Junk
   @Junk2
   public static class Thing
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
      @Junk
      public Thing getStyle()
      {
         Thing thing = new Thing();
         thing.setName("bill");
         return thing;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestService.class);
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
