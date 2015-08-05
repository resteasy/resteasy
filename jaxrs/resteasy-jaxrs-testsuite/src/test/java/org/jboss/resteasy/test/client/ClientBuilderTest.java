package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.io.ByteArrayOutputStream;
import javax.ws.rs.core.Response;
import java.lang.reflect.Modifier;
import java.util.logging.*;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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
   
   @Test
   public void addAndRemovePropertyTest() throws Exception
   {
      String property = "prop";
      Client client = ClientBuilder.newClient();
      client.property(property, property);
      Object p = client.getConfiguration().getProperty(property);
      Assert.assertEquals("prop", (String)p);
      try {
         client.property(property, null);
      } catch (NullPointerException e) {
        Assert.fail("Couldn't remove property.");
      }
      p = client.getConfiguration().getProperty(property);
      Assert.assertEquals(null, p);
   }

   @Test(expected=IllegalStateException.class)
   public void closeClientSendRequestTest() throws Exception
   {
      Client client = ClientBuilder.newClient();
      client.close();
      client.target(generateURL("/"));
   }

   @Test(expected=IllegalStateException.class)
   public void closeClientWebTargetTest() throws Exception
   {
      Client client = ClientBuilder.newClient();
      WebTarget base = client.target(generateURL("/") + "/test");
      client.close();
      Response response = base.request().get();
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
      Logger logger = Logger.getLogger(ResteasyProviderFactory.class.getName());

      Formatter formatter = new SimpleFormatter();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Handler handler = new StreamHandler(out, formatter);
      logger.addHandler(handler);

      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getClasses().size();
      try {
         client.register(FeatureReturningFalse.class).register(FeatureReturningFalse.class);
         handler.flush();
         String logMsg = out.toString();

         Assert.assertNotNull(logMsg);
         Assert.assertTrue(logMsg.contains("Provider class"));
         Assert.assertTrue(logMsg.contains("is already registered."));
      } finally {
         logger.removeHandler(handler);
      }
      Assert.assertEquals(count + 1, client.getConfiguration().getClasses().size());

      client.close();

   }

   @Test
   public void testDoubleRegistration()
   {
      Logger logger = Logger.getLogger(ResteasyProviderFactory.class.getName());

      Formatter formatter = new SimpleFormatter();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Handler handler = new StreamHandler(out, formatter);
      logger.addHandler(handler);

      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getInstances().size();
      Object reg = new FeatureReturningFalse();
      try {
         client.register(reg).register(reg);
         handler.flush();
         String logMsg = out.toString();

         Assert.assertNotNull(logMsg);
         Assert.assertTrue(logMsg.contains("Provider instance"));
         Assert.assertTrue(logMsg.contains("is already registered."));
      } finally {
         logger.removeHandler(handler);
      }
      Assert.assertEquals(count + 1, client.getConfiguration().getInstances().size());

      client.close();

   }
}
