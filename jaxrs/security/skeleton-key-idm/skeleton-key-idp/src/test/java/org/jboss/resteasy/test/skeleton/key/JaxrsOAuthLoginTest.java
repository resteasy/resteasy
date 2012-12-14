package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.apache.http.client.params.CookiePolicy;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
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
   private static final RealmConfiguration config = new RealmConfiguration();

   @BeforeClass
   public static void setupTest() throws Exception
   {
      setupIDM("testrealm.json");
      ResourceMetadata resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm("test-realm");
      resourceMetadata.setResourceName("Application");
      resourceMetadata.setRealmKey(realmInfo.getPublicKey());
      config.setAuthUrl(UriBuilder.fromUri(realmInfo.getAuthorizationUrl()));
      config.setClient(new ResteasyClient());
      config.setCodeUrl(config.getClient().target(realmInfo.getCodeUrl()));
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
      String uri = generateURL("/Application/user.txt");
      ResteasyWebTarget target = client.target(uri);
      Response res = target.request().get();
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

      Pattern sp = Pattern.compile("name=\"scope\" value=\"([^\"]+)\"");
      matcher = sp.matcher(form);
      String scope = null;
      if (matcher.find())
      {
         scope = matcher.group(1);
      }

      sp = Pattern.compile("name=\"redirect_uri\" value=\"([^\"]+)\"");
      matcher = sp.matcher(form);
      String redirect = null;
      if (matcher.find())
      {
         redirect = matcher.group(1);
      }


      Form loginform = new Form()
              .param("username", "wburke")
              .param("Password", "userpassword")
              .param("client_id", "loginclient");
      if (scope != null) loginform.param("scope", scope);
      if (redirect != null) loginform.param("redirect_uri", redirect);

      res = client.target(loginUrl).request().post(Entity.form(loginform));
      Assert.assertEquals(302, res.getStatus());
      location = res.getLocation();
      System.out.println("size: " + location.toString().length());
      System.out.println(location);
      res.close();
      res = client.target(location).request().get();
      Assert.assertEquals(200, res.getStatus());
      String val = res.readEntity(String.class);
      Assert.assertEquals("user", val);
      NewCookie cookie = res.getCookies().get(config.getSessionCookieName());
      Assert.assertNotNull(cookie);
      res.close();

      // Apache HC 4 saves cookies so create a new client just to make sure cookie stuff is cool.
      ResteasyClient client = new ResteasyClient();
      ResteasyWebTarget t = client.target(uri);
      res = t.request().get();
      Assert.assertEquals(302, res.getStatus());
      res.close();
      val = t.request().cookie(config.getSessionCookieName(), cookie.getValue()).get(String.class);
      Assert.assertEquals("user", val);
      res = t.request().get();
      Assert.assertEquals(302, res.getStatus());
      res.close();

      res = client.target(generateURL("/Application/admin.txt")).request().cookie(config.getSessionCookieName(), cookie.getValue()).get();
      Assert.assertEquals(403, res.getStatus());
      res.close();

      client.close();

   }
}
