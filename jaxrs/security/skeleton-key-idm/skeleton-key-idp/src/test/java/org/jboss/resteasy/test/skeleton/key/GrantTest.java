package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.idm.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.AccessTokenResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrantTest extends SkeletonTestBase
{
   @BeforeClass
   public static void setupTest() throws Exception
   {
      setupIDM("testrealm.json");
   }

   @Test
   public void testSuccessfulToken() throws Exception
   {

      Form form = new Form();
      form.param(RequiredCredential.PASSWORD, "userpassword")
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
      SkeletonKeyTokenVerification verification = RSATokenVerifier.verify((X509Certificate[])null, tokenResponse.getToken(), metadata);


   }

   @Test
   public void testPem() throws Exception
   {
      System.out.println("*******************");
      if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      StringWriter writer = new StringWriter();
      PEMWriter pemWriter = new PEMWriter(writer);
      pemWriter.writeObject(keyPair.getPublic());
      pemWriter.flush();
      String s = writer.toString();
      System.out.println(s);
      s = PemUtils.removeBeginEnd(s);

      PublicKey pk = PemUtils.decodePublicKey(s);
      Assert.assertEquals(pk, keyPair.getPublic());
   }




}
