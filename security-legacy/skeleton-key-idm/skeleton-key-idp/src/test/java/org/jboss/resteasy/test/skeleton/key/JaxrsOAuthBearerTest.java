package org.jboss.resteasy.test.skeleton.key;

import org.junit.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.jaxrs.JaxrsBearerTokenFilter;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsOAuthBearerTest extends SkeletonTestBase
{
   private static final RealmConfiguration config = new RealmConfiguration();

   @BeforeClass
   public static void setupTest() throws Exception
   {
      setupIDM("testrealm.json");
      final ResourceMetadata resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm("test-realm");
      resourceMetadata.setResourceName("Application");
      resourceMetadata.setRealmKey(realmInfo.getPublicKey());
      config.setAuthUrl(UriBuilder.fromUri(realmInfo.getAuthorizationUrl()));
      config.setClient(new ResteasyClientBuilder().build());
      config.setCodeUrl(config.getClient().target(realmInfo.getCodeUrl()));
      config.setSslRequired(false);
      config.setClientId("loginclient");
      config.getCredentials().param("Password", "clientpassword");
      config.setMetadata(resourceMetadata);

      deployment.getProviderFactory().register(new DynamicFeature()
      {
         @Override
         public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
         {
            if (resourceInfo.getResourceClass().equals(MyApplication.class))
            {
               configurable.register(new JaxrsBearerTokenFilter(resourceMetadata));
            }
         }
      });
      deployment.getProviderFactory().register(RoleBasedSecurityFeature.class);
      deployment.getRegistry().addPerRequestResource(MyApplication.class);
   }

   @Path("/Application")
   public static class MyApplication
   {
      @Path("/user.txt")
      @Produces("text/plain")
      @RolesAllowed("user")
      @GET
      public String getUser()
      {
         return "user";
      }

      @Path("/admin.txt")
      @Produces("text/plain")
      @RolesAllowed("admin")
      @GET
      public String getAdmin()
      {
         return "admin";
      }
   }

   @Test
   public void testLogin() throws Exception
   {
      String uri = generateURL("/Application/user.txt");
      Form loginform = new Form()
              .param("client_id", "wburke")
              .param("Password", "userpassword");
      AccessTokenResponse res = client.target(realmInfo.getGrantUrl()).request().post(Entity.form(loginform), AccessTokenResponse.class);
      String token = res.getToken();
      String txt = client.target(uri).request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get(String.class);
      Response response = client.target(generateURL("/Application/admin.txt")).request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
      Assert.assertEquals(403, response.getStatus());
   }
}
