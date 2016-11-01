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
      ClientBuilder client = ClientBuilder.newBuilder();
      Assert.assertEquals(ResteasyClientBuilder.class.getName(), client.getClass().getName());
   }

   @Test
   public void testInContainerClientBuilder() throws Exception
   {
      ClientBuilder client = ClientBuilder.newBuilder();
      Assert.assertEquals(ResteasyClientBuilder.class.getName(), client.getClass().getName());
   }

   @Test
   @RunAsClient
   @Ignore
   public void testRuntimeDelegate() throws Exception
   {
      RuntimeDelegate.setInstance(null);
      RuntimeDelegate rd = RuntimeDelegate.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class.getName(), rd.getClass().getName());
      RuntimeDelegate.setInstance(null);
   }

   @Test
   public void testInContainerRuntimeDegate() throws Exception
   {
      RuntimeDelegate.setInstance(null);
      RuntimeDelegate rd = RuntimeDelegate.getInstance();
      Assert.assertEquals(ResteasyProviderFactory.class.getName(), rd.getClass().getName());
      RuntimeDelegate.setInstance(null);
   }

}
