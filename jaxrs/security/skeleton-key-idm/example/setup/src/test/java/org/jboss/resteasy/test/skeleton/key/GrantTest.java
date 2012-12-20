package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.RequiredCredentialRepresentation;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrantTest
{
   @Test
   public void testSuccessfulToken() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testrealm.json");
      Response res
              = client.target("https://localhost:8443/skeleton-key/realms").request().post(Entity.json(is));
      Assert.assertEquals(201, res.getStatus());
      PublishedRealmRepresentation realmInfo = res.readEntity(PublishedRealmRepresentation.class);
      Form form = new Form();
      form.param(RequiredCredentialRepresentation.PASSWORD, "userpassword")
              .param("client_id", "wburke");
      System.out.println(realmInfo.getGrantUrl());
      Response response = client.target(realmInfo.getGrantUrl()).request().post(Entity.form(form));
      if (response.getStatus() != 200)
      {
         Assert.fail(response.readEntity(String.class));
      }
      AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
      Assert.assertEquals("bearer", tokenResponse.getTokenType());
      ResourceMetadata metadata = new ResourceMetadata();
      metadata.setRealm("test-realm");
      metadata.setResourceName("Application");
      metadata.setRealmKey(realmInfo.getPublicKey());
      SkeletonKeyTokenVerification verification = RSATokenVerifier.verify((X509Certificate[]) null, tokenResponse.getToken(), metadata);


   }
}
