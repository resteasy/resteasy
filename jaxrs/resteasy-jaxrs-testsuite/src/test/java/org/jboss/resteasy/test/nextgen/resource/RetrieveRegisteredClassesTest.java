package org.jboss.resteasy.test.nextgen.resource;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RetrieveRegisteredClassesTest extends BaseResourceTest
{
   @Path("/testResource")
   @Produces(MediaType.APPLICATION_XML)
   public static final class TestResource
   {

      @GET
      public String get()
      {
         return TestResource.class.getName();
      }

   }

   private static class MyFilter implements ClientRequestFilter
   {

      // To discard empty constructor
      private MyFilter(Object value)
      {
      }

      @Override
      public void filter(ClientRequestContext clientRequestContext) throws IOException
      {
      }

   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().register(MyFilter.class);
   }

   @Test
   public void testMatch()
   {
      Client client = ClientBuilder.newClient();
      try {
          String uri = generateURL("/testResource");
          MyFilter myFilter = new MyFilter(new Object());

          WebTarget firstWebTarget = client.target(uri).register(myFilter);
          String firstResult = firstWebTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
          Configuration firstWebTargetConfiguration = firstWebTarget.getConfiguration();
          Set<Class<?>> classes = firstWebTargetConfiguration.getClasses();
          Set<Object> instances = firstWebTargetConfiguration.getInstances();
          Assert.assertFalse(classes.contains(MyFilter.class));
          Assert.assertTrue(instances.contains(myFilter));

          WebTarget secondWebTarget = client.target(uri);
          Configuration secondWebTargetConfiguration = secondWebTarget.getConfiguration();
          for (Class<?> classz : classes) {
              if (!secondWebTargetConfiguration.isRegistered(classz)) {
                  secondWebTarget.register(classz);
              }
          }
          for (Object instance : instances) {
              if (!secondWebTargetConfiguration.isRegistered(instance.getClass())) {
                  secondWebTarget.register(instance);
              }
          }
          String secondeResult = secondWebTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
          classes = secondWebTargetConfiguration.getClasses();
          instances = secondWebTargetConfiguration.getInstances();
          Assert.assertFalse(classes.contains(MyFilter.class));
          Assert.assertTrue(instances.contains(myFilter));
          Assert.assertEquals(firstResult, secondeResult);
      } finally {
          client.close();
      }

   }

}
