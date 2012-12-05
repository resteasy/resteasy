package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ServiceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.model.representations.AccessTokenResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LoginTest extends SkeletonTestBase
{
   @BeforeClass
   public static void setupTest() throws Exception
   {
      setupIDM("testrealm.json");
   }

   @Test
   public void testLogin() throws Exception
   {
      System.out.println(realmInfo.getAuthorizationUrl());
      WebTarget authUrl = client.target(realmInfo.getAuthorizationUrl())
              .queryParam("client_id", "loginclient")
              .queryParam("redirect_uri", "http://localhost:8081/loginclient/redirect");

      String form = authUrl.request().get(String.class);
      System.out.println(form);

      Pattern p = Pattern.compile("action=\"([^\"]+)\"");
      Matcher matcher = p.matcher(form);
      String loginUrl = null;
      if (matcher.find())
      {
         loginUrl = matcher.group(1);
      }
      Assert.assertNotNull(loginUrl);

      Form loginform = new Form()
              .param("username", "wburke")
              .param("Password", "userpassword")
              .param("client_id", "loginclient")
              .param("redirect_uri", "http://localhost:8081/loginclient/redirect");

      System.out.println("LoginUrl: " + loginUrl);
      Response response = client.target(loginUrl).request().post(Entity.form(loginform));
      Assert.assertEquals(302, response.getStatus());


   }

}
