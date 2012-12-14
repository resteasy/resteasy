package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.jaxrs.JaxrsOAuthLoginFilter;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsOAuthLoginTest extends SkeletonTestBase
{
   @BeforeClass
   public static void setupTest() throws Exception
   {
      setupIDM("testrealm.json");
      final RealmConfiguration config = new RealmConfiguration();
      ResourceMetadata resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm("test-realm");
      resourceMetadata.setResourceName("Application");
      resourceMetadata.setRealmKey(realmInfo.getPublicKey());
      config.setAuthUrl(UriBuilder.fromUri(realmInfo.getAuthorizationUrl()));
      config.setCodeUrl(client.target(realmInfo.getCodeUrl()));
      config.setClient(client);
      config.setCookieSecure(false);
      config.setSslRequired(false);
      config.setClientId("loginclient");
      config.getCredentials().param("Password", "clientpassword");
      config.setMetadata(resourceMetadata);

      deployment.getProviderFactory().register(new DynamicFeature()
      {
         @Override
         public void configure(ResourceInfo resourceInfo, Configurable configurable)
         {
            if (resourceInfo.getResourceClass().equals(MyApplication.class))
            {
               configurable.register(new JaxrsOAuthLoginFilter(config));
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
      Response res = client.target(generateURL("/Application/user.txt")).request().get();
      Assert.assertEquals(res.getStatus(), 302);
      res.close();
      URI location = res.getLocation();
      System.out.println(location);
      res = client.target(location).request().get();
      Assert.assertEquals(200, res.getStatus());
      String form = res.readEntity(String.class);
      System.out.println(form);

      Pattern p = Pattern.compile("action=\"([^\"]+)\"");
      Matcher matcher = p.matcher(form);
      String loginUrl = null;
      if (matcher.find())
      {
         loginUrl = matcher.group(1);
      }
      Assert.assertNotNull(loginUrl);

      res.close();
   }
}
