package org.jboss.resteasy.test.dynamic;

import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1083
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date February 13, 2016
 */
public class ClientDynamicFeaturesTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @ConstrainedTo(RuntimeType.CLIENT)
   public static class ClientFeature1 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }
   
   @ConstrainedTo(RuntimeType.CLIENT)
   public static class ClientFeature2 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }
   
   @ConstrainedTo(RuntimeType.SERVER)
   public static class ServerFeature1 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }

   @ConstrainedTo(RuntimeType.SERVER)
   public static class ServerFeature2 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }
   
   public static class DualFeature1 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }

   public static class DualFeature2 implements DynamicFeature
   {
      @Override
      public void configure(ResourceInfo resourceInfo, FeatureContext context)
      {  
      }
   }
   
   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Test
   public void testDynamicFeatures() throws Exception
   {
      ResteasyProviderFactory factory = dispatcher.getProviderFactory();
      factory.registerProvider(ClientFeature1.class, 0, false, null);
      factory.registerProvider(ServerFeature1.class, 0, false, null);
      factory.registerProvider(DualFeature1.class, 0, false, null);
      ClientFeature2 clientFeature = new ClientFeature2();
      ServerFeature2 serverFeature = new ServerFeature2();
      DualFeature2 feature = new DualFeature2();
      factory.registerProviderInstance(clientFeature, null, 0, false);
      factory.registerProviderInstance(serverFeature, null, 0, false);
      factory.registerProviderInstance(feature, null, 0, false);
      Set<DynamicFeature> clientFeatureSet = factory.getClientDynamicFeatures();
      Set<DynamicFeature> serverFeatureSet = factory.getServerDynamicFeatures();
      
      Assert.assertEquals(1, countFeatures(clientFeatureSet, "ClientFeature1"));
      Assert.assertEquals(1, countFeatures(clientFeatureSet, "ClientFeature2"));
      Assert.assertEquals(1, countFeatures(clientFeatureSet, "DualFeature1"));
      Assert.assertEquals(1, countFeatures(clientFeatureSet, "DualFeature2"));
      Assert.assertEquals(1, countFeatures(serverFeatureSet, "ServerFeature1"));
      Assert.assertEquals(1, countFeatures(serverFeatureSet, "ServerFeature2"));
      Assert.assertEquals(1, countFeatures(serverFeatureSet, "DualFeature1"));
      Assert.assertEquals(1, countFeatures(serverFeatureSet, "DualFeature2"));
   }
   
   private int countFeatures(Set<DynamicFeature> featureSet, String feature)
   {
      int count = 0;
      for (Iterator<DynamicFeature> it = featureSet.iterator(); it.hasNext(); )
      {
         Class<?> clazz = it.next().getClass();
         if (clazz.getName().contains(feature))
         {
            count++;
         }
      }
      return count;
   }
}
