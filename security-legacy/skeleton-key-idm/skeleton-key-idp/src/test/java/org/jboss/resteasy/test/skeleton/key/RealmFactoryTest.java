package org.jboss.resteasy.test.skeleton.key;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.skeleton.key.SkeletonKeyContextResolver;
import org.jboss.resteasy.skeleton.key.idm.adapters.infinispan.InfinispanIDM;
import org.jboss.resteasy.skeleton.key.idm.service.RealmFactory;
import org.jboss.resteasy.skeleton.key.representations.idm.RealmRepresentation;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RealmFactoryTest
{

   private static NettyJaxrsServer server;
   private static ResteasyDeployment deployment;
   private static InfinispanIDM idm;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      deployment = server.getDeployment();
      idm = new InfinispanIDM(SkeletonTestBase.getDefaultCache());
      RealmFactory factory = new RealmFactory(idm);
      deployment.getProviderFactory().register(new SkeletonKeyContextResolver(true));
      deployment.getRegistry().addSingletonResource(factory);
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
      deployment = null;
   }

   public Registry getRegistry()
   {
      return deployment.getRegistry();
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return deployment.getProviderFactory();
   }

   /**
    * @param resource
    */
   public static void addPerRequestResource(Class<?> resource)
   {
      deployment.getRegistry().addPerRequestResource(resource);
   }
   
   @Test
   public void testGoodRealmCreation() throws Exception
   {
      RealmRepresentation realm = SkeletonTestBase.loadJson("testrealm.json");

      Client client = new ResteasyClientBuilder().build();
      WebTarget target = client.target(generateURL("/realms"));
      Response response = target.request().post(Entity.json(realm));
      Assert.assertEquals(201, response.getStatus());
      response.close();
      client.close();

   }


}
