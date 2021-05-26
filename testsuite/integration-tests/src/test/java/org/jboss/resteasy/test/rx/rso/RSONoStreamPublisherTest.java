package org.jboss.resteasy.test.rx.rso;

import static org.junit.Assert.assertArrayEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.rx.rso.resource.RSONoStreamPublisherResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RSONoStreamPublisherTest
{
   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RSONoStreamPublisherTest.class.getSimpleName());
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, RSONoStreamPublisherResource.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RSONoStreamPublisherTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   @Test
   @Ignore
   public void testSingle() throws InterruptedException
   {
      String data = client.target(generateURL("/single")).request().get(String.class);
      Assert.assertEquals("got it", data);

      String[] data2 = client.target(generateURL("/observable")).request().get(String[].class);
      Assert.assertArrayEquals(new String[] {"one", "two"}, data2);

      data2 = client.target(generateURL("/flowable")).request().get(String[].class);
      Assert.assertArrayEquals(new String[] {"one", "two"}, data2);

      data = client.target(generateURL("/context/single")).request().get(String.class);
      Assert.assertEquals("got it", data);

      data2 = client.target(generateURL("/context/observable")).request().get(String[].class);
      Assert.assertArrayEquals(new String[] {"one", "two"}, data2);

      data2 = client.target(generateURL("/context/flowable")).request().get(String[].class);
      assertArrayEquals(new String[] {"one", "two"}, data2);
   }
}
