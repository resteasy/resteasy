package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.SkeletonKeyContextResolver;
import org.jboss.resteasy.skeleton.key.idm.adapters.infinispan.InfinispanIDM;
import org.jboss.resteasy.skeleton.key.idm.service.RealmFactory;
import org.jboss.resteasy.skeleton.key.idm.service.TokenManagement;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.RealmRepresentation;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonTestBase extends BaseResourceTest
{
   protected static InfinispanIDM idm;
   protected static WebTarget realm;
   protected static ResteasyClient client;
   protected static PublishedRealmRepresentation realmInfo;

   public static void setupIDM(String realmJson) throws Exception
   {
      idm = new InfinispanIDM(getDefaultCache());
      RealmFactory factory = new RealmFactory(idm);
      deployment.getProviderFactory().register(new SkeletonKeyContextResolver(true));
      deployment.getRegistry().addSingletonResource(factory);

      TokenManagement tokenManagement = new TokenManagement(idm);
      deployment.getRegistry().addSingletonResource(tokenManagement);
      RealmRepresentation r = loadJson(realmJson);

      client = new ResteasyClientBuilder().build();
      WebTarget target = client.target(generateURL("/realms"));
      Response response = target.request().post(Entity.json(r));
      Assert.assertEquals(201, response.getStatus());
      Assert.assertNotNull(response.getLocation());
      realm = client.target(response.getLocation());
      realmInfo = response.readEntity(PublishedRealmRepresentation.class);
      response.close();
   }

   @AfterClass
   public static void closeClient() throws Exception
   {
      client.close();
   }

   public static Cache getDefaultCache()
   {
      EmbeddedCacheManager manager = new DefaultCacheManager();
      manager.defineConfiguration("custom-cache", new ConfigurationBuilder()
              .eviction().strategy(EvictionStrategy.NONE).maxEntries(5000)
              .build());
      return manager.getCache("custom-cache");
   }

   public static RealmRepresentation loadJson(String path) throws IOException
   {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      int c;
      while ( (c = is.read()) != -1)
      {
         os.write(c);
      }
      byte[] bytes = os.toByteArray();
      System.out.println(new String(bytes));

      return JsonSerialization.fromBytes(RealmRepresentation.class, bytes);
   }
}
