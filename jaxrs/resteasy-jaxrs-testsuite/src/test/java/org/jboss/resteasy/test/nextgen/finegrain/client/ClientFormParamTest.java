package org.jboss.resteasy.test.nextgen.finegrain.client;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

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

      @Override
      public Form post(Form form)
      {
         return form;
      }
   }

   @Path("/form")
   public static interface FormResource
   {
      @POST
      public String put(@FormParam("value") String value);

      @POST
      @Path("object")
      @Produces(MediaType.APPLICATION_FORM_URLENCODED)
      @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
      Form post(Form form);
   }

   @Test
   public void test() throws Exception
   {
      final Dispatcher dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(FormResourceImpl.class);
         ResteasyClient client = new ResteasyClientBuilder().build();
         final FormResource proxy = ProxyBuilder.builder(FormResource.class, client.target(generateBaseUrl())).build();
         final String result = proxy.put("value");
         Assert.assertEquals(result, "value");
         final String result1 = client.target(generateURL("/form")).request().post(Entity.form(new Form().param("value", "value")), String.class);
         Assert.assertEquals(result1, "value");

         Form form = new Form().param("bill", "burke").param("foo", "bar");
         Form rtn = proxy.post(form);
         Assert.assertEquals(rtn.asMap().size(), form.asMap().size());
         Assert.assertEquals(rtn.asMap().getFirst("bill"), "burke");
         Assert.assertEquals(rtn.asMap().getFirst("foo"), "bar");

      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}