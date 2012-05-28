package org.jboss.resteasy.test.form;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.*;

public class ComplexFormTest extends BaseResourceTest
{

   public static class Person
   {

      @FormParam("name")
      private String name;

      @Form(prefix = "invoice")
      private Address invoice;

      @Form(prefix = "shipping")
      private Address shipping;

      @Override
      public String toString()
      {
         return new StringBuilder("name:'").append(name).append("', invoice:'").append(invoice.street).append("', shipping:'").append(shipping.street).append("'").toString();
      }
   }

   public static class Address
   {

      @FormParam("street")
      private String street;
   }

   @Path("person")
   public static class MyResource
   {

      @POST
      @Produces(MediaType.TEXT_PLAIN)
      @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
      public String post(@Form Person p)
      {
         return p.toString();
      }
   }

   @Before
   public void register()
   {
      deployment.getRegistry().

              addPerRequestResource(MyResource.class);
   }

   @Test
   public void shouldSupportNestedForm() throws Exception
   {
      MockHttpResponse response = new MockHttpResponse();
      MockHttpRequest request = MockHttpRequest.post("person").accept(MediaType.TEXT_PLAIN).contentType(MediaType.APPLICATION_FORM_URLENCODED);
      request.addFormHeader("name", "John Doe");
      request.addFormHeader("invoice.street", "Main Street");
      request.addFormHeader("shipping.street", "Station Street");
      dispatcher.invoke(request, response);

      assertEquals("name:'John Doe', invoice:'Main Street', shipping:'Station Street'", response.getContentAsString());
   }
}
