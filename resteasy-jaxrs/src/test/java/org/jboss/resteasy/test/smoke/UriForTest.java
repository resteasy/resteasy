package org.jboss.resteasy.test.smoke;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.Assert;

import org.jboss.resteasy.client.core.UriFor;
import org.jboss.resteasy.spi.MappedBy;
import org.junit.Test;

public class UriForTest
{
   @Path("/test/{id:\\d+}")
   public interface TestResource
   {
      @GET
      IdObject getById(@PathParam("id") Long id);
   }

   public static class ValueObject
   {
      private Long id;
      private String firstName;
      private String lastName;

      public ValueObject(Long id)
      {
         this.id = id;
      }

      public ValueObject(String firstName, String lastName)
      {
         super();
         this.firstName = firstName;
         this.lastName = lastName;
      }

      public Long getId()
      {
         return id;
      }

      public void setId(Long id)
      {
         this.id = id;
      }

      public String getFirstName()
      {
         return firstName;
      }

      public void setFirstName(String firstName)
      {
         this.firstName = firstName;
      }

      public String getLastName()
      {
         return lastName;
      }

      public void setLastName(String lastName)
      {
         this.lastName = lastName;
      }
   }

   @MappedBy(resourceClass = TestResource.class, resourceMethodName = "getById")
   public static class IdObject extends ValueObject
   {
      public IdObject(Long id)
      {
         super(id);
      }
   }

   @MappedBy(template = "test/{lastName}/{firstName}")
   public static class NameObject extends ValueObject
   {
      public NameObject(String firstName, String lastName)
      {
         super(firstName, lastName);
      }
   }

   @Test
   public void testId()
   {
      NameObject joeSmith = new NameObject("Joe", "Smith");
      assertEquals("/foo/Joe/Smith", UriFor.uriForObject(joeSmith,
            "/foo/{firstName}/{lastName}"));
      assertEquals("/test/5", UriFor.uriForObject(new IdObject(5l)));
      assertEquals("/test/Smith/Joe", UriFor.uriForObject(joeSmith));
   }

   private void assertEquals(String string, URI uri)
   {
      Assert.assertEquals(string, uri.toString());
   }
}
