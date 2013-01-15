package org.jboss.resteasy.example.oauth;

import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.servlet.ServletOAuthClient;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.BasicAuthHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProductDatabaseClient
{
   public static List<String> getProducts()
   {
      ResteasyClient client = new ResteasyClientBuilder()
                 .disableTrustManager() // shouldn't really do this, but I'm being lazy
                 .build();

      String authheader = BasicAuthHelper.createHeader("bburke@redhat.com", "password");
      Form form = new Form().param("grant_type", "client_credentials");
      AccessTokenResponse res = client.target("https://localhost:8443/auth-server/j_oauth_token_grant").request()
              .header(HttpHeaders.AUTHORIZATION, authheader)
              .post(Entity.form(form), AccessTokenResponse.class);


      try
      {
         Response response = client.target("https://localhost:8443/database/products").request()
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + res.getToken()).get();
         return response.readEntity(new GenericType<List<String>>(){});
      }
      finally
      {
         client.close();
      }
   }
}
