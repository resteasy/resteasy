package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.skeleton.key.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.adapters.infinispan.InfinispanIDM;
import org.jboss.resteasy.skeleton.key.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.model.representations.RealmRepresentation;
import org.jboss.resteasy.skeleton.key.service.RealmFactory;
import org.jboss.resteasy.skeleton.key.service.SkeletonKeyContextResolver;
import org.jboss.resteasy.skeleton.key.service.TokenManagement;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TokenTest extends BaseResourceTest
{
   private static InfinispanIDM idm;
   private static WebTarget realm;
   private static Client client;

   @BeforeClass
   public static void setupIDM() throws Exception
   {
      idm = new InfinispanIDM(RealmFactoryTest.getDefaultCache());
      RealmFactory factory = new RealmFactory(idm);
      deployment.getProviderFactory().register(new SkeletonKeyContextResolver(true));
      deployment.getRegistry().addSingletonResource(factory);

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      TokenManagement tokenManagement = new TokenManagement(idm, keyPair.getPrivate(), keyPair.getPublic());
      deployment.getRegistry().addSingletonResource(tokenManagement);
      RealmRepresentation r = RealmFactoryTest.loadJson();

      client = new ResteasyClient();
      WebTarget target = client.target(generateURL("/realms"));
      Response response = target.request().post(Entity.json(r));
      Assert.assertEquals(201, response.getStatus());
      Assert.assertNotNull(response.getLocation());
      realm = client.target(response.getLocation());
      response.close();
   }

   @AfterClass
   public static void closeClient() throws Exception
   {
      client.close();
   }

   @Test
   public void testSuccessfulToken() throws Exception
   {
      Form form = new Form();
      form.param(RequiredCredential.PASSWORD, "userpassword")
          .param("client_id", "wburke");
      Response response = realm.path("tokens").request().post(Entity.form(form));
      if (response.getStatus() != 200)
      {
         Assert.fail(response.readEntity(String.class));
      }
      System.out.println(response.readEntity(String.class));
      //SkeletonKeyToken token = response.readEntity(SkeletonKeyToken.class);

   }




}
