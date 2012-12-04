package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.adapters.infinispan.InfinispanIDM;
import org.jboss.resteasy.skeleton.key.model.representations.RealmRepresentation;
import org.jboss.resteasy.skeleton.key.service.RealmFactory;
import org.jboss.resteasy.skeleton.key.service.SkeletonKeyContextResolver;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
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
public class RealmFactoryTest extends BaseResourceTest
{


   protected static Cache getDefaultCache()
   {
      EmbeddedCacheManager manager = new DefaultCacheManager();
      manager.defineConfiguration("custom-cache", new ConfigurationBuilder()
              .eviction().strategy(EvictionStrategy.NONE).maxEntries(5000)
              .build());
      return manager.getCache("custom-cache");
   }

   private static InfinispanIDM idm;

   @BeforeClass
   public static void setupIDM() throws Exception
   {
      idm = new InfinispanIDM(getDefaultCache());
      RealmFactory factory = new RealmFactory(idm);
      deployment.getProviderFactory().register(new SkeletonKeyContextResolver(true));
      deployment.getRegistry().addSingletonResource(factory);
   }

   @Test
   public void testGoodRealmCreation() throws Exception
   {
      RealmRepresentation realm = loadJson();

      Client client = new ResteasyClient();
      WebTarget target = client.target(generateURL("/realms"));
      Response response = target.request().post(Entity.json(realm));
      Assert.assertEquals(201, response.getStatus());
      response.close();
      client.close();

   }

   public static RealmRepresentation loadJson() throws IOException
   {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testrealm.json");
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
