package org.jboss.resteasy.test.keystone;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.keystone.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.keystone.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.keystone.model.Authentication;
import org.jboss.resteasy.keystone.model.Mappers;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.model.User;
import org.jboss.resteasy.keystone.server.Loader;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TokenTest
{
   private static final Logger LOG = Logger.getLogger(TokenTest.class);
   private static NettyJaxrsServer server;
   private static ResteasyDeployment deployment;
   private static PrivateKey privateKey;
   private static X509Certificate certificate;

   public static class SApp extends Application
   {
      SkeletonKeyApplication app;

      public SApp(@Context Configurable<?> confgurable)
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

      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.setDeployment(deployment);
      server.start();
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
      LOG.info(new Loader().export(app.getCache()));

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
         LOG.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
      }

   }

   @AfterClass
   public static void after() throws Exception
   {
      server.stop();
      server = null;
      deployment = null;
   }

   @Test
   public void testAuth() throws Exception
   {
      // Use our own providerFactory to test json context provider
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      RegisterBuiltin.register(providerFactory);
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(providerFactory).build();
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map<String, String> creds = new HashMap<>();
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
      client.close();
   }

   @Test
   public void testSignedAuth() throws Exception
   {
      // Use our own providerFactory to test json context provider
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      RegisterBuiltin.register(providerFactory);
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(providerFactory).build();
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map<String, String> creds = new HashMap<>();
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
      LOG.info(signed);
      PKCS7SignatureInput input = new PKCS7SignatureInput(signed);
      input.setCertificate(certificate);
      Assert.assertTrue(input.verify());
      client.close();
   }


   @Test
   public void testNotAuthenticated()
   {
      {
         // assuming @RolesAllowed is on class level. Too lazy to test it all!
         String newUser = "{ \"user\" : { \"username\" : \"wburke\", \"name\" : \"Bill Burke\", \"email\" : \"bburke@redhat.com\", \"enabled\" : true, \"credentials\" : { \"password\" : \"geheim\" }} }";
         ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
         Response response = client.target(generateURL("/users")).request().post(Entity.json(newUser));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();
         client.close();
      }
      {
         String newRole = "{ \"role\" : { \"name\" : \"admin\"} }";
         ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
         Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();
         client.close();

      }
      {
         String newProject = "{ \"project\" : { \"id\" : \"5\", \"name\" : \"Resteasy\", \"description\" : \"The Best of REST\", \"enabled\" : true } }";
         ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
         Response response = client.target(generateURL("/projects")).request().post(Entity.json(newProject));
         Assert.assertEquals(response.getStatus(), 403);
         response.close();
         client.close();
      }
   }

   @Test
   public void testCMD() throws Exception
   {
      Authentication auth = new SkeletonKeyClientBuilder().username("wburke").password("geheim").authentication("Skeleton Key");
      ResteasyClient client = new ResteasyClientBuilder().build();
      Mappers.registerContextResolver(client);
      WebTarget target = client.target(generateBaseUrl());
      String tiny = target.path("tokens").path("url").request().post(Entity.json(auth), String.class);
      LOG.info(tiny);
      LOG.info("tiny.size: " + tiny.length());
      Security.addProvider(new BouncyCastleProvider());


      KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
      PrivateKey privateKey = keyPair.getPrivate();
      X509Certificate cert = KeyTools.generateTestCertificate(keyPair);

      byte[] signed = p7s(privateKey, cert, null, tiny.getBytes());


      CMSSignedData data = new CMSSignedData(signed);
//      byte[] bytes = (byte[])data.getSignedContent().getContent();
//      System.out.println("BYTES: " + new String(bytes));
//      System.out.println("size:" + signed.length);
//      int length = Base64.encodeBytes(signed).length();
//      System.out.println("Base64.size: " + length);

      SignerInformation signer = data.getSignerInfos().getSigners().iterator().next();
      boolean valid = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert.getPublicKey()));
      LOG.info("valid: " + valid);
      client.close();


   }


   private static byte[] p7s(PrivateKey priv, X509Certificate storecert, CertStore certs, byte[] contentbytes) throws CertStoreException, CMSException, NoSuchAlgorithmException, NoSuchProviderException, IOException, OperatorCreationException, CertificateEncodingException {
      CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
      ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(priv);

      signGen.addSignerInfoGenerator(
              new JcaSignerInfoGeneratorBuilder(
                      new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                      .build(sha1Signer, storecert));

      CMSTypedData content = new CMSProcessableByteArray(contentbytes);

      CMSSignedData signedData = signGen.generate(content, true);

      return signedData.getEncoded();
   }


}
