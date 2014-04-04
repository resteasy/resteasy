package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientBuilderTest
{

   @Test
   public void entityStringThrowsExceptionWhenUnparsableTest() throws Exception {
      try {
         Entity.entity("entity", "\\//\\");
         Assert.fail();
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testBuilder() throws Exception
   {
      String property = "prop";
      Client client = ClientBuilder.newClient();
      client.property(property, property);
      Configuration config = client.getConfiguration();
      client = ClientBuilder.newClient(config);

   }

   public static void inner() throws Exception
   {
      Feature feature = new Feature() {
         @Override
         public boolean configure(FeatureContext context)
         {
            return false;
         }
      };

      System.out.println("is static: " + Modifier.isStatic(feature.getClass().getModifiers()));
      Client client = ClientBuilder.newClient();
      client.register(feature.getClass());

   }

   //@Test
   public void testInnerFeature() throws Exception
   {
      // TCK uses anonymous non-static inner classes to test.  BOGUS POOP!
      System.out.println("non-static");
      inner();
      System.out.println("non-static");
      Feature feature = new Feature() {
         @Override
         public boolean configure(FeatureContext context)
         {
            return false;
         }
      };
      System.out.println("is static: " + Modifier.isStatic(feature.getClass().getModifiers()));

      Client client = ClientBuilder.newClient();
      client.register(feature.getClass());


   }

   public static class FeatureReturningFalse implements Feature {
      @Override
      public boolean configure(FeatureContext context) {
         // false returning feature is not to be registered
         return false;
      }
   }

   @Test
   public void testDoubleClassRegistration()
   {
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getClasses().size();
      client.register(FeatureReturningFalse.class).register(FeatureReturningFalse.class);
      Assert.assertEquals(count + 1, client.getConfiguration().getClasses().size());
      client.close();

   }

   @Test
   public void testDoubleRegistration()
   {
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getInstances().size();
      Object reg = new FeatureReturningFalse();
      client.register(reg);
      client.register(reg);
      Assert.assertEquals(count + 1, client.getConfiguration().getInstances().size());
      client.close();

   }



}
