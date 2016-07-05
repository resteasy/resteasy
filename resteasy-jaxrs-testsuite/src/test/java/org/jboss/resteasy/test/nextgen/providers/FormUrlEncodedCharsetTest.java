package org.jboss.resteasy.test.nextgen.providers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-477
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class FormUrlEncodedCharsetTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static MediaType testMediaType8 = MediaType.APPLICATION_FORM_URLENCODED_TYPE.withCharset(StandardCharsets.UTF_8.displayName());
   protected static MediaType testMediaType16 = MediaType.APPLICATION_FORM_URLENCODED_TYPE.withCharset(StandardCharsets.UTF_16.displayName());
   protected static String charsetName;
   protected static String alephBetGimel = "אבג";
   
   @Path("test")
   public static class TestResource
   {
      @POST
      public Response form(MultivaluedMap<String, String> form) throws UnsupportedEncodingException
      {
         String s = form.getFirst("name");
         System.out.println("s: " + s);      
         return Response.ok().entity(s).build();
      }
   }
   
   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testFormDefault() throws UnsupportedEncodingException
   {
      Client client = ClientBuilder.newClient();;
      WebTarget target = client.target("http://localhost:8081/test");
      Form form = new Form();
      form.param("name", alephBetGimel);
      Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = target.request().post(entity);
      String result = response.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals(result, alephBetGimel);
   }
   
   @Test
   public void testFormUTF8() throws UnsupportedEncodingException
   {
      Client client = ClientBuilder.newClient();;
      WebTarget target = client.target("http://localhost:8081/test");
      Form form = new Form();
      form.param("name", alephBetGimel);
      Entity<Form> entity = Entity.entity(form, testMediaType8);
      Response response = target.request().post(entity);
      String result = response.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals(result, alephBetGimel);
   }
   
   @Test
   public void testFormUTF16() throws UnsupportedEncodingException
   {
      Client client = ClientBuilder.newClient();;
      WebTarget target = client.target("http://localhost:8081/test");
      Form form = new Form();
      form.param("name", alephBetGimel);
      Entity<Form> entity = Entity.entity(form, testMediaType16);
      Response response = target.request().post(entity);
      String result = response.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals(result, alephBetGimel);
   }
}
