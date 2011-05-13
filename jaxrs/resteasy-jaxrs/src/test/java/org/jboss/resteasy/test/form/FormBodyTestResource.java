package org.jboss.resteasy.test.form;

import junit.framework.Assert;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public class FormBodyTestResource extends BaseResourceTest
{

   public static class MyForm
   {
      @Body
      public String body;
   }

   @Path("body")
   public static class MyResource
   {
      @PUT
      @Consumes("text/plain")
      @Produces("text/plain")
      public String put(@Form MyForm form)
      {
         return form.body + ".gotIt";
      }
   }

   @Path("body")
   public static interface MyClient
   {
      @PUT
      @Consumes("text/plain")
      @Produces("text/plain")
      public String put(String value);
   }

   @Before
   public void setup()
   {
      addPerRequestResource(MyResource.class);
   }

   @Test
   public void test()
   {
      MyClient client = TestPortProvider.createProxy(MyClient.class);
      Assert.assertEquals("foo.gotIt", client.put("foo"));
   }
}
