package org.jboss.resteasy.test.core;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.old.ResteasyProviderFactory;
import org.jboss.resteasy.test.core.basic.resource.AcceptLanguagesResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResteasyJAXRSImplTest
{

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(ResteasyJAXRSImplTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, AcceptLanguagesResource.class);
   }

   @Test
   @RunAsClient
   public void testClientBuilder() throws Exception
   {
      testClientBuilderNewBuilder();
   }

   @Test
   public void testInContainerClientBuilder() throws Exception
   {
      testClientBuilderNewBuilder();
   }

   @Test
   @RunAsClient
   public void testRuntimeDelegate() throws Exception
   {
      testRuntimeDelegateGetInstance();
      testResteasyProviderFactoryGetInstance();
      testResteasyProviderFactoryNewInstance();
   }

   @Test
   public void testInContainerRuntimeDegate() throws Exception
   {
      testRuntimeDelegateGetInstance();
      testResteasyProviderFactoryGetInstance();
      testResteasyProviderFactoryNewInstance();
   }
   
   private void testClientBuilderNewBuilder() {
      ClientBuilder client = ClientBuilder.newBuilder();
      Assert.assertEquals(ResteasyClientBuilder.class.getName(), client.getClass().getName());
   }
   
   private void testRuntimeDelegateGetInstance() {
      RuntimeDelegate.setInstance(null);
      RuntimeDelegate rd = RuntimeDelegate.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class.getName(), rd.getClass().getName());
      RuntimeDelegate.setInstance(null);
   }
   
   private void testResteasyProviderFactoryGetInstance() {
      ResteasyProviderFactory.setInstance(null);
      ResteasyProviderFactory rpf = resteasyProviderFactoryGetInstance();
      Assert.assertEquals(rpf, ResteasyProviderFactory.getInstance());
      ResteasyProviderFactory.setInstance(null);
      ResteasyProviderFactory rpf2 = resteasyProviderFactoryGetInstance();
      Assert.assertNotEquals(rpf, rpf2);
      ResteasyProviderFactory.setInstance(null);
   }
   
   private void testResteasyProviderFactoryNewInstance() {
      ResteasyProviderFactory.setInstance(null);
      ResteasyProviderFactory rpf = resteasyProviderFactoryNewInstance();
      ResteasyProviderFactory rpf2 = resteasyProviderFactoryNewInstance();
      ResteasyProviderFactory rpf3 = resteasyProviderFactoryNewInstance();
      Assert.assertNotEquals(rpf, rpf2);
      Assert.assertNotEquals(rpf, rpf3);
      Assert.assertNotEquals(rpf2, rpf3);
      
      ResteasyProviderFactory rpfGI = resteasyProviderFactoryGetInstance();
      Assert.assertNotEquals(rpfGI, rpf3);
   }
   
   private static ResteasyProviderFactory resteasyProviderFactoryGetInstance() {
      Object o = org.jboss.resteasy.spi.ResteasyProviderFactory.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, o.getClass());
      return (ResteasyProviderFactory)o;
   }


   private static ResteasyProviderFactory resteasyProviderFactoryNewInstance() {
      Object o = org.jboss.resteasy.spi.ResteasyProviderFactory.newInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, o.getClass());
      return (ResteasyProviderFactory)o;
   }
}
