package org.jboss.resteasy.test.finegrain.client;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class ClientFormParamTest
{

   public static class FormResourceImpl implements FormResource
   {
      public String put(String value)
      {
         return value;
      }
   }

   @Path("/form")
   public static interface FormResource
   {
      @POST
      public String put(@FormParam("value") String value);
   }

   @Test
   public void test() throws Exception
   {
      final Dispatcher dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(FormResourceImpl.class);
         final FormResource client = ProxyFactory.create(FormResource.class, "http://localhost:8081");
         final String result = client.put("value");
         Assert.assertEquals(result, "value");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
}