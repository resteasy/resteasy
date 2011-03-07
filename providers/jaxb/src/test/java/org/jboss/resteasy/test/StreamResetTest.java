package org.jboss.resteasy.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamResetTest extends BaseResourceTest
{
   @Path("/test")
   public static class SimpleResource
   {
      @GET
      @Produces("application/xml")
      public String get()
      {
         return "<person name=\"bill\"/>";
      }
   }

   @XmlRootElement(name = "person")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Person
   {
      private String name;

      @XmlAttribute
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @XmlRootElement(name = "place")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Place
   {
      private String name;

      @XmlAttribute
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(SimpleResource.class);
   }

   @Test
   public void test456() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test"));
      ClientResponse<Place> response = request.get(Place.class);
      boolean exceptionThrown = false;
      try
      {
         Place place = response.getEntity();

      }
      catch (Exception e)
      {
         exceptionThrown = true;
      }
      Assert.assertTrue(exceptionThrown);

      response.resetStream();

      Person person = response.getEntity(Person.class);
      Assert.assertNotNull(person);
      Assert.assertEquals("bill", person.getName());
   }

}
