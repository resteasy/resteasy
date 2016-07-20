package org.jboss.resteasy.test.finegrain.client;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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
      final Dispatcher dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(FormResourceImpl.class);
         Client client = ClientBuilder.newClient();
         final FormResource proxy = ((ResteasyWebTarget) client.target(generateBaseUrl())).proxy(FormResource.class);
         final String result = proxy.put("value");
         Assert.assertEquals(result, "value");
         final Response response = client.target(generateURL("/form")).request().post(Entity.form(new Form("value", "value")));
         final String result1 = response.readEntity(String.class);
         Assert.assertEquals(result1, "value");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}