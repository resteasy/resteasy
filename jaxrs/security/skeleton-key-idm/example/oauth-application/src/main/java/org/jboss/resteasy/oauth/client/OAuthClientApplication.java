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
      List<PublishedRealmRepresentation> realms = client.target("https://localhost:8443/skeleton-key/realms?name=test-realm")
              .request()
              .get(new GenericType<List<PublishedRealmRepresentation>>() {});
      singletons.add(new AppResource(realms.get(0)));
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
