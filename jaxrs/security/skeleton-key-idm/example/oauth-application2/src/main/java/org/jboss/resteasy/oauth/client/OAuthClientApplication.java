package org.jboss.resteasy.oauth.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ApplicationPath("/services")
public class OAuthClientApplication extends Application
{
   protected Set<Object> singletons = new HashSet<Object>();

   public OAuthClientApplication()
   {
      ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
      PublishedRealmRepresentation realm = client.target("https://localhost:8443/application3/j_oauth_realm_info")
              .request()
              .get(PublishedRealmRepresentation.class);
      singletons.add(new AppResource(realm));
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
