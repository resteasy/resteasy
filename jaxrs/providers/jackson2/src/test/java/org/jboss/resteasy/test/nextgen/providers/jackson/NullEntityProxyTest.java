package org.jboss.resteasy.test.nextgen.providers.jackson;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1684
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 13, 2017
 */
public class NullEntityProxyTest extends BaseResourceTest
{
   @Resource
   @Path("")
   public interface Hello
   {
      @POST
      @Path("json")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.TEXT_PLAIN)
      public String json(Car car);
   }
   
   @Resource
   @Path("")
   public static class HelloImpl implements Hello
   {
      @POST
      @Path("json")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.TEXT_PLAIN)
      public String json(Car car)
      {
         return car == null ? "null" : car.getColor();
      }
   }
   
   public static class Car
   {   
      private String color;

      public String getColor() {
         return color;
      }
      public void setColor(String color) {
         this.color = color;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(HelloImpl.class);
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testNullParameter()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL(""));
      Hello hello = target.proxy(Hello.class);
      Car car = new Car();
      car.setColor("blue");
      String color = hello.json(car);
      Assert.assertEquals("should be \"blue\"", "blue", color);
      color = hello.json(null);
      Assert.assertEquals("should be \"null\"", "null", color);  
   }
}
