package org.jboss.resteasy.test.nextgen.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1057
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 27, 2015
 */
public class NullEntityTest extends BaseResourceTest
{
   @Resource
   @Path("")
   public static class Hello
   {
      @POST
      @Path("null")
      @Produces("text/plain")
      public String post(String entity)
      {
         System.out.println("null post: \"" + entity + "\"");
         return entity;
      }
      
      @POST
      @Path("entity")
      @Produces("text/plain")
      public String empty(String entity)
      {
         System.out.println("entity: \"" + entity + "\"");
         return entity;
      }
      
      @POST
      @Path("form")
      @Consumes("application/x-www-form-urlencoded")
      @Produces("text/plain")
      public String form(@FormParam("s") String s)
      {
         System.out.println("s: \"" + s + "\"");
         return s;
      }
      
      @POST
      @Path("html")
      @Consumes("text/html")
      @Produces("text/plain")
      public String html(String html)
      {
         System.out.println("html: \"" + html + "\"");
         return html;
      }
      
      @POST
      @Path("xhtml")
      @Consumes("application/xhtml+xml")
      @Produces("text/plain")
      public String xhtml(String xhtml)
      {
         System.out.println("xhtml: \"" + xhtml + "\"");
         return xhtml;
      }
      
      @POST
      @Path("xml")
      @Consumes("application/xml")
      @Produces("text/plain")
      public String xml(String xml)
      {
         System.out.println("xml: \"" + xml + "\"");
         return xml;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Hello.class);
   }

   @Test
   public void testPostNull()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/null"));
      String response = target.request().post(null, String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("", response);
      client.close();
   }
   
   @Test
   public void testEntity()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/entity"));
      String response = target.request().post(Entity.entity(null, MediaType.WILDCARD), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("", response);
      client.close();
   }
   
   @Test
   public void testForm()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/form"));
      String response = target.request().post(Entity.form((Form) null), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(null, response);
      client.close();
   }
   
   @Test
   public void testHtml()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/html"));
      String response = target.request().post(Entity.html(null), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("", response);
      client.close();
   }
   
   @Test
   public void testXhtml()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/xhtml"));
      String response = target.request().post(Entity.xhtml(null), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("", response);
      client.close();
   }
   
   @Test
   public void testXml()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/xml"));
      String response = target.request().post(Entity.xml(null), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("", response);
      client.close();
   }
}
