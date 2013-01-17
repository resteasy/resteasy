package org.jboss.resteasy.example.oauth;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
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

      Form form = new Form().param("grant_type", "client_credentials");
      ResteasyWebTarget target = client.target("https://localhost:8443/auth-server/j_oauth_token_grant");
      // this is resteasy specific, check spec to make sure it hasn't added a way to do basic auth
      target.configuration().register(new BasicAuthentication("bburke@redhat.com", "password"));
      AccessTokenResponse res = target
              .request()
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
