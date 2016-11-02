package org.jboss.resteasy.test.core;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.core.basic.resource.AcceptLanguagesResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
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
   @Ignore
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
      ResteasyProviderFactory rpf = ResteasyProviderFactory.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, rpf.getClass());
      Assert.assertEquals(rpf, ResteasyProviderFactory.getInstance());
      ResteasyProviderFactory.setInstance(null);
      ResteasyProviderFactory rpf2 = ResteasyProviderFactory.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, rpf2.getClass());
      Assert.assertNotEquals(rpf, rpf2);
      ResteasyProviderFactory.setInstance(null);
   }
   
   private void testResteasyProviderFactoryNewInstance() {
      ResteasyProviderFactory.setInstance(null);
      ResteasyProviderFactory rpf = ResteasyProviderFactory.newInstance();
      ResteasyProviderFactory rpf2 = ResteasyProviderFactory.newInstance();
      ResteasyProviderFactory rpf3 = ResteasyProviderFactory.newInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, rpf.getClass());
      Assert.assertEquals(ResteasyProviderFactory.class, rpf2.getClass());
      Assert.assertEquals(ResteasyProviderFactory.class, rpf3.getClass());
      Assert.assertNotEquals(rpf, rpf2);
      Assert.assertNotEquals(rpf, rpf3);
      Assert.assertNotEquals(rpf2, rpf3);
      
      ResteasyProviderFactory rpfGI = ResteasyProviderFactory.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class, rpfGI.getClass());
      Assert.assertNotEquals(rpfGI, rpf3);
   }

}
