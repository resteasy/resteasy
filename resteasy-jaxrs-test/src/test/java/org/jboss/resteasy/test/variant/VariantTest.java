package org.jboss.resteasy.test.variant;

import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Variant;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
* RESTEASY-994
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $$Revision: 1.1 $$
*
* Copyright November 29, 2013
*/
public class VariantTest
{
   public static final MediaType WILDCARD_WITH_PARAMS;
   public static final MediaType TEXT_HTML_WITH_PARAMS;
   public static final MediaType TEXT_PLAIN_WITH_PARAMS;
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   static
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("q", "0.5");
      params.put("a", "1");
      params.put("b", "2");
      params.put("c", "3");
      WILDCARD_WITH_PARAMS = new MediaType("*", "*", params);
      
      params.clear();
      params.put("a", "1");
      params.put("b", "2");
      params.put("c", "3");
      TEXT_HTML_WITH_PARAMS = new MediaType("text", "html", params);
      
      params.clear();
      params.put("a", "1");
      params.put("b", "2");
      params.put("c", "3");
      TEXT_PLAIN_WITH_PARAMS = new MediaType("text", "plain", params);
   }
   
   @Path("")
   public static class TestResource
   {
      @Context
      private Request request;

      @Context
      private HttpHeaders headers;


      @GET
      @Path("variant")
      public String variant()
      {
         List<Variant> variants = Variant.mediaTypes(TEXT_PLAIN_TYPE, TEXT_HTML_TYPE).build();
         MediaType selected = request.selectVariant(variants).getMediaType();
         System.out.println("selected media type: " + selected);
         return selected.toString();
      }

      @GET
      @Path("params")
      public String params()
      {
         List<Variant> variants = Variant.mediaTypes(TEXT_PLAIN_WITH_PARAMS, TEXT_HTML_WITH_PARAMS).build();
         MediaType selected = request.selectVariant(variants).getMediaType();
         System.out.println("selected media type: " + selected);
         return selected.toString();
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   /**
    * Verifies that a more specific media type is preferred.
    */
   @Test
   public void testVariant() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/variant"));
      request.accept(MediaType.WILDCARD_TYPE);
      request.accept(MediaType.TEXT_HTML_TYPE);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertEquals(MediaType.TEXT_HTML, entity);
   }
   
   /**
    * Verifies that the number of parameters does not outweigh more specific media types.
    */
   @Test
   public void testVariantWithParameters() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/params"));
      request.accept(WILDCARD_WITH_PARAMS);
      request.accept(MediaType.TEXT_HTML_TYPE);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertEquals(TEXT_HTML_WITH_PARAMS.toString(), entity);
   }
}