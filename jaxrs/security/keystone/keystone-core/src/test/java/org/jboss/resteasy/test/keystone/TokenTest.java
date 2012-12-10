package org.jboss.resteasy.test.keystone;

import junit.framework.Assert;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.keystone.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.keystone.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.keystone.model.Authentication;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.model.User;
import org.jboss.resteasy.keystone.server.Loader;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.Base64;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TokenTest
{
   private static ResteasyDeployment deployment;
   private static PrivateKey privateKey;
   private static X509Certificate certificate;

   public static class SApp extends Application
   {
      SkeletonKeyApplication app;

      public SApp(@Context Configurable confgurable)
      {
         this.app = new SkeletonKeyApplication(confgurable);
      }



      @Override
      public Set<Object> getSingletons()
      {
         return app.getSingletons();
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      deployment.setApplicationClass(SApp.class.getName());

      EmbeddedContainer.start(deployment);
      SkeletonKeyApplication app = ((SApp)deployment.getApplication()).app;

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
      privateKey = keyPair.getPrivate();
      certificate = KeyTools.generateTestCertificate(keyPair);
      app.getTokenService().setCertificate(certificate);
      app.getTokenService().setPrivateKey(privateKey);

      StoredUser admin = new StoredUser();
      admin.setName("Bill");
      admin.setUsername("wburke");
      HashMap<String, String> creds = new HashMap<String, String>();
      creds.put("password", "geheim");
      admin.setCredentials(creds);
      app.getUsers().create(admin);

      Project project = new Project();
      project.setName("Skeleton Key");
      project.setEnabled(true);
      app.getProjects().createProject(project);

      Role adminRole = new Role();
      adminRole.setName("admin");
      app.getRoles().create(adminRole);

      app.getProjects().addUserRole(project.getId(), admin.getId(), adminRole.getId());

      // Test export/import
      System.out.println(new Loader().export(app.getCache()));

      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         new Loader().export(app.getCache(), baos);
         ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
         app.getCache().clear();
         new Loader().importStore(bios, app.getCache());
      }
      catch (Exception e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testAuth() throws Exception
   {
      // Use our own providerFactory to test json context provider
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      RegisterBuiltin.register(providerFactory);
      ResteasyClient client = new ResteasyClient(providerFactory);
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map creds = new HashMap();
      creds.put("password", "foobar");
      newUser.setCredentials(creds);
      Response response = admin.users().create(newUser);
      User user = response.readEntity(User.class);
      response = admin.roles().create("user");
      Role role = response.readEntity(Role.class);
      Projects projects = admin.projects().query("Skeleton Key");
      Project project = projects.getList().get(0);
      admin.projects().addUserRole(project.getId(), user.getId(), role.getId());

      admin = new SkeletonKeyClientBuilder().username("jsmith").password("foobar").idp(target).admin();
      response = admin.roles().create("error");
      Assert.assertEquals(403, response.getStatus());
   }

   @Test
   public void testSignedAuth() throws Exception
   {
      // Use our own providerFactory to test json context provider
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      RegisterBuiltin.register(providerFactory);
      ResteasyClient client = new ResteasyClient(providerFactory);
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map creds = new HashMap();
      creds.put("password", "foobar");
      newUser.setCredentials(creds);
      Response response = admin.users().create(newUser);
      User user = response.readEntity(User.class);
      response = admin.roles().create("user");
      Role role = response.readEntity(Role.class);
      Projects projects = admin.projects().query("Skeleton Key");
      Project project = projects.getList().get(0);
      admin.projects().addUserRole(project.getId(), user.getId(), role.getId());

      String signed = new SkeletonKeyClientBuilder().username("jsmith").password("foobar").idp(target).obtainSignedToken("Skeleton Key");
      System.out.println(signed);
      PKCS7SignatureInput input = new PKCS7SignatureInput(signed);
      input.setCertificate(certificate);
      Assert.assertTrue(input.verify());
   }


   @Test
   public void testNotAuthenticated()
   {
      {
         // assuming @RolesAllowed is on class level. Too lazy to test it all!
         String newUser = "{ \"user\" : { \"username\" : \"wburke\", \"name\" : \"Bill Burke\", \"email\" : \"bburke@redhat.com\", \"enabled\" : true, \"credentials\" : { \"password\" : \"geheim\" }} }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/users")).request().post(Entity.json(newUser));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();
      }
      {
         String newRole = "{ \"role\" : { \"name\" : \"admin\"} }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();

      }
      {
         String newProject = "{ \"project\" : { \"id\" : \"5\", \"name\" : \"Resteasy\", \"description\" : \"The Best of REST\", \"enabled\" : true } }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/projects")).request().post(Entity.json(newProject));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();
      }
   }

   @Test
   public void testCMD() throws Exception
   {
      Authentication auth = new SkeletonKeyClientBuilder().username("wburke").password("geheim").authentication("Skeleton Key");
      ResteasyClient client = new ResteasyClient();
      WebTarget target = client.target(generateBaseUrl());
      String tiny = target.path("tokens").path("url").request().post(Entity.json(auth), String.class);
      System.out.println(tiny);
      System.out.println("tiny.size: " + tiny.length());
      Security.addProvider(new BouncyCastleProvider());


      KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
      PrivateKey privateKey = keyPair.getPrivate();
      X509Certificate cert = KeyTools.generateTestCertificate(keyPair);

      byte[] signed = p7s(privateKey, cert, null, tiny.getBytes());


      CMSSignedData data = new CMSSignedData(signed);
      byte[] bytes = (byte[])data.getSignedContent().getContent();
      System.out.println("BYTES: " + new String(bytes));
      System.out.println("size:" + signed.length);
      System.out.println("Base64.size: " + Base64.encodeBytes(signed).length());

      SignerInformation signer = (SignerInformation)data.getSignerInfos().getSigners().iterator().next();
      System.out.println("valid: " + signer.verify(cert, "BC"));


   }


   private static byte[] p7s(PrivateKey priv, X509Certificate storecert, CertStore certs, byte[] contentbytes) throws CertStoreException, CMSException, NoSuchAlgorithmException, NoSuchProviderException, IOException
   {
      CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
      signGen.addSigner(priv, (X509Certificate)storecert, CMSSignedDataGenerator.DIGEST_SHA512);
      //signGen.addCertificatesAndCRLs(certs);
      CMSProcessable content = new CMSProcessableByteArray(contentbytes);

      CMSSignedData signedData = signGen.generate(content, true, "BC");
      return signedData.getEncoded();
   }


}
