package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.skeleton.key.SkeletonKeyContextResolver;
import org.jboss.resteasy.skeleton.key.idm.adapters.infinispan.InfinispanIDM;
import org.jboss.resteasy.skeleton.key.idm.service.RealmFactory;
import org.jboss.resteasy.skeleton.key.representations.idm.RealmRepresentation;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RealmFactoryTest extends BaseResourceTest
{


   private static InfinispanIDM idm;

   @BeforeClass
   public static void setupIDM() throws Exception
   {
      idm = new InfinispanIDM(SkeletonTestBase.getDefaultCache());
      RealmFactory factory = new RealmFactory(idm);
      deployment.getProviderFactory().register(new SkeletonKeyContextResolver(true));
      deployment.getRegistry().addSingletonResource(factory);
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
