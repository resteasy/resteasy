package org.jboss.resteasy.test.nextgen;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-929
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright April 27, 2015
 */
public class EmbeddedMultipartTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   protected static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");

   @XmlRootElement(name = "customer")
   public static class Customer
   {
      private String name;

      public Customer()
      {
      }

      public Customer(String name)
      {
         this.name = name;
      }

      @XmlElement
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }
   
   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Resource
   @Path("")
   public static class TestResource
   {
      @Path("embedded")
      @POST
      @Consumes("multipart/mixed")
      @Produces(MediaType.TEXT_PLAIN)
      public Response post(MultipartInput input) throws Exception
      {
         InputPart inputPart = input.getParts().iterator().next();
         MultipartInput multipart = inputPart.getBody(MultipartInput.class, null);
         inputPart = multipart.getParts().iterator().next();
         Customer customer = inputPart.getBody(Customer.class, null);
         System.out.println("customer: " + customer.getName());
         return Response.ok(customer.getName()).build();
      }

      @Path("customer")
      @POST
      @Consumes("multipart/mixed")
      @Produces(MediaType.TEXT_PLAIN)
      public Response postCustomer(MultipartInput input) throws IOException
      {
         InputPart part = input.getParts().iterator().next();
         Customer customer = part.getBody(Customer.class, null);
         System.out.println("customer: " + customer.getName());
         return Response.ok(customer.getName()).build();
      }
      
      @Path("invalid")
      @POST
      @Consumes("multipart/mixed")
      @Produces(MediaType.TEXT_PLAIN)
      public Response postInvalid(MultipartInput input) throws IOException
      {
         InputPart part = input.getParts().iterator().next();
         Object o = part.getBody(TestResource.class, null);
         return Response.ok(o).build();
      }
   }
   
   @Test
   public void testEmbedded()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/embedded"));
      Customer customer = new Customer("bill");
      MultipartOutput innerPart = new MultipartOutput();
      innerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
      MultipartOutput outerPart = new MultipartOutput();
      outerPart.addPart(innerPart, MULTIPART_MIXED);
      Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
      String response = target.request().post(entity, String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("bill", response);
      client.close();
   }
   
   @Test
   public void testCustomer()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/customer"));
      Customer customer = new Customer("bill");
      MultipartOutput outerPart = new MultipartOutput();
      outerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
      Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
      String response = target.request().post(entity, String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("bill", response);
      client.close();
   }
   
   @Test
   public void testInvalid()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      try
      {
         ResteasyWebTarget target = client.target(generateURL("/invalid"));
         Customer customer = new Customer("bill");
         MultipartOutput outerPart = new MultipartOutput();
         outerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
         Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
         target.request().post(entity, String.class);
      }
      catch (InternalServerErrorException e)
      {
         Response response = e.getResponse();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(500, response.getStatus());
      }
      finally
      {
         client.close();
      }
   }
}
