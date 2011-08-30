package org.jboss.resteasy.tests.smime;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SMIMETest
{
   private static PrivateKey privateKey;
   private static X509Certificate cert;

   @BeforeClass
   public static void setup() throws Exception
   {
      InputStream certPem = Thread.currentThread().getContextClassLoader().getResourceAsStream("cert.pem");
      Assert.assertNotNull(certPem);
      cert = PemUtils.decodeCertificate(certPem);

      InputStream privatePem = Thread.currentThread().getContextClassLoader().getResourceAsStream("private.pem");
      privateKey = PemUtils.decodePrivateKey(privatePem);


   }

   @Test
   public void testEncryptedGet() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/encrypted");
      EnvelopedInput input = request.getTarget(EnvelopedInput.class);
      Customer cust = (Customer)input.getEntity(Customer.class, privateKey, cert);
      System.out.println("Encrypted Message From Server:");
      System.out.println(cust);
   }
   @Test
   public void testEncryptedPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/encrypted");
      Customer cust = new Customer();
      cust.setName("Bill");
      EnvelopedOutput output = new EnvelopedOutput(cust, "application/xml");
      output.setCertificate(cert);
      ClientResponse res = request.body("application/pkcs7-mime", output).post();
      Assert.assertEquals(204, res.getStatus());

   }

   @Test
   public void testSigned() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/signed");
      SignedInput input = request.getTarget(SignedInput.class);
      Customer cust = (Customer)input.getEntity(Customer.class);
      System.out.println("Signed Message From Server: ");
      System.out.println(cust);
      input.verify(cert);

   }

   @Test
   public void testSignedPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/signed");
      Customer cust = new Customer();
      cust.setName("Bill");
      SignedOutput output = new SignedOutput(cust, "application/xml");
      output.setPrivateKey(privateKey);
      output.setCertificate(cert);
      ClientResponse res = request.body("multipart/signed", output).post();
      Assert.assertEquals(204, res.getStatus());

   }

   @Test
   public void testEncryptedAndSignedGet() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/encrypted/signed");
      EnvelopedInput enveloped = request.getTarget(EnvelopedInput.class);
      SignedInput signed = (SignedInput)enveloped.getEntity(SignedInput.class, privateKey, cert);
      Customer cust = (Customer)signed.getEntity(Customer.class);
      System.out.println(cust);
      Assert.assertTrue(signed.verify(cert));
   }

   @Test
   public void testEncryptedSignedPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/smime/encrypted/signed");
      Customer cust = new Customer();
      cust.setName("Bill");
      SignedOutput signed = new SignedOutput(cust, "application/xml");
      signed.setPrivateKey(privateKey);
      signed.setCertificate(cert);
      EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
      output.setCertificate(cert);
      ClientResponse res = request.body("application/pkcs7-mime", output).post();
      Assert.assertEquals(204, res.getStatus());
   }


}
