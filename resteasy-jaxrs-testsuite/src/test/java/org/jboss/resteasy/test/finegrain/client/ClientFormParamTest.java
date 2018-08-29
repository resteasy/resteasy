package org.jboss.resteasy.test.finegrain.client;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.jboss.resteasy.test.TestPortProvider.createClientRequest;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

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
   public interface FormResource
   {
      @POST
      String put(@FormParam("value") String value);
   }

   @Test
   public void test() throws Exception
   {
      final Dispatcher dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(FormResourceImpl.class);
         final FormResource client = ProxyFactory.create(FormResource.class, generateBaseUrl());
         final String result = client.put("value");
         Assert.assertEquals(result, "value");
         final String result1 = createClientRequest("/form").formParameter("value", "value").post(
                 String.class).getEntity();
         Assert.assertEquals(result1, "value");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}