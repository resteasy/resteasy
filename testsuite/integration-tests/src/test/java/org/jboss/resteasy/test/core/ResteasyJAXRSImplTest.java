package org.jboss.resteasy.test.core;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxrs implementation
 * @tpChapter Integration tests
 * @tpTestCaseDetails RESTEASY-1531
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
public class ResteasyJAXRSImplTest
{

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(ResteasyJAXRSImplTest.class.getSimpleName());
      war.addClass(NotForForwardCompatibility.class);
      return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
   }

   /**
    * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   @RunAsClient
   public void testClientBuilder() throws Exception
   {
      testClientBuilderNewBuilder();
   }

   /**
    * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
    * is bundled in the server.
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testInContainerClientBuilder() throws Exception
   {
      testClientBuilderNewBuilder();
   }

   /**
    * @tpTestDetails Tests RuntimeDelegate instance implementation with ResteasyProviderFactory
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   @RunAsClient
   public void testRuntimeDelegate() throws Exception
   {
      testRuntimeDelegateGetInstance();
      testResteasyProviderFactoryGetInstance();
      testResteasyProviderFactoryNewInstance();
   }

   /**
    * @tpTestDetails Tests RuntimeDelegate instance implementation with ResteasyProviderFactory in the container.
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   @Category({NotForForwardCompatibility.class})
   public void testInContainerRuntimeDelegate() throws Exception
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
