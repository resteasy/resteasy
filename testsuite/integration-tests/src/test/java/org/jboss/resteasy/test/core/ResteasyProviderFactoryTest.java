package org.jboss.resteasy.test.core;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;

@RunWith(Arquillian.class)
@RunAsClient
public class ResteasyProviderFactoryTest
{
   public interface HelloResourceInterface
   {
      @GET
      @Produces("text/plain")
      @Path("/hello")
      String hello();

      @GET
      @Path("/some/{id}")
      Single<String> single(@PathParam("id") String id);

      @GET
      @Path("/some/{id}")
      CompletionStage<String> cs(@PathParam("id") String id);
   }

   static ResteasyClient client;

   @Deployment(name = "dep_A")
   public static Archive<?> deployA()
   {
      WebArchive war = TestUtil.prepareArchive(ResteasyProviderFactoryTest.class.getSimpleName() + "_A");
      war.addClass(HelloResource.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null);
   }

   @Deployment(name = "dep_B")
   public static Archive<?> deployB()
   {
      WebArchive war = TestUtil.prepareArchive(ResteasyProviderFactoryTest.class.getSimpleName() + "_B");
      war.addClass(HelloResource.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null);
   }

   @Before
   public void init()
   {
      client = (ResteasyClient) ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception
   {
      client.close();
   }

   private String generateURL(String path, String suffix)
   {
      return PortProviderUtil.generateURL(path, ResteasyProviderFactoryTest.class.getSimpleName() + suffix);
   }

   /**
    * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   @RunAsClient
   public void testClientBuilder() throws Exception
   {
      HelloResourceInterface proxyA = client.target(generateURL("/", "_A"))
            .proxyBuilder(HelloResourceInterface.class).build();
      HelloResourceInterface proxyB = client.target(generateURL("/", "_B"))
            .proxyBuilder(HelloResourceInterface.class).build();

      Assert.assertNotEquals(proxyA.hello(), proxyB.hello());
   }

}
