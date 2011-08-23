package org.jboss.resteasy.test.security.smime;

import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.jboss.resteasy.security.PemUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SignedTest
{

   private static X509Certificate cert;
   private static PrivateKey privateKey;
   private static PrivateKey badKey;

   @BeforeClass
   public static void setup() throws Exception
   {
      Security.addProvider(new BouncyCastleProvider());
      InputStream certIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert.der");
      cert = PemUtils.getCertificateFromDer(certIs);

      InputStream privateIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert-private.der");
      privateKey = PemUtils.getPrivateFromDer(privateIs);

      InputStream badIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("private_dkim_key.der");
      badKey = PemUtils.getPrivateFromDer(badIs);
   }

   private MimeBodyPart createMsg() throws MessagingException
   {
      InternetHeaders ih = new InternetHeaders();
      ih.addHeader("Content-Type", "application/xml");

      return new MimeBodyPart(ih, "<customer name=\"bill\"/>".getBytes());
   }

   private void output(MimeBodyPart mp) throws IOException, MessagingException
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);
      String s = new String(os.toByteArray());
      System.out.println(s);
   }

   private void output(MimeMultipart mp) throws IOException, MessagingException
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);
      String s = new String(os.toByteArray());
      System.out.println(s);
   }

   @Test
   public void testMultipart() throws Exception
   {
      MimeMultipart mp = new MimeMultipart();
      InternetHeaders ih = new InternetHeaders();
      ih.addHeader("Content-Type", "text/xml");
      MimeBodyPart bp = new MimeBodyPart(ih, "<customer/>".getBytes());
      mp.addBodyPart(bp);

      bp = new MimeBodyPart(ih, "<product/>".getBytes());
      mp.addBodyPart(bp);



      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);
      String s = new String(os.toByteArray());

      System.out.println(s);
      System.out.println("************");

      String contentType = mp.getContentType();
      contentType = contentType.replace("\r\n", "").replace("\t", " ");
      System.out.println("Content-Type: " + contentType);

      mp = new MimeMultipart(new ByteArrayDataSource(s.getBytes(), "multipart/signed"));
      System.out.println("count: " + mp.getCount());


   }


   @Test
   public void testPythonSigned() throws Exception
   {
      final InputStream pythonIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("python_signed.txt");

      /*
      Message message = new Message(pythonIs);
      Multipart multipart = (Multipart)message.getBody();
      System.out.println("count: " + multipart.getCount());
      */

      ByteArrayDataSource ds = new ByteArrayDataSource(pythonIs, "multipart/signed");
      MimeMultipart mm = new MimeMultipart(ds);

      System.out.println(mm.getContentType());

      System.out.println("Multipart.count(): " + mm.getCount());

      MimeBodyPart mbp = (MimeBodyPart) mm.getBodyPart(0);

      output(mbp);

      SMIMESigned signed = new SMIMESigned(mm);

      SignerInformationStore signers = signed.getSignerInfos();
      Assert.assertEquals(1, signers.size());
      SignerInformation signer = (SignerInformation) signers.getSigners().iterator().next();
      Assert.assertTrue(signer.verify(cert.getPublicKey(), "BC"));
   }

   @Test
   public void testOutput() throws Exception
   {
      SMIMESignedGenerator gen = new SMIMESignedGenerator();
      SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
      gen.addSignerInfoGenerator(signer);

      MimeMultipart mp = gen.generate(createMsg());

      output(mp);

   }

   @Test
   public void testOutput2() throws Exception
   {
      SMIMESignedGenerator gen = new SMIMESignedGenerator();
      SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
      gen.addSignerInfoGenerator(signer);

      MimeMultipart mp = gen.generate(createMsg());

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);

      ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
      String contentType = mp.getContentType();
      contentType = contentType.replace("\r\n", "").replace("\t", " ");

      ByteArrayDataSource ds = new ByteArrayDataSource(is, contentType);
      MimeMultipart mm = new MimeMultipart(ds);
      MimeBodyPart part = (MimeBodyPart)mm.getBodyPart(0);




   }

   @Test
   public void testPythonVerified() throws Exception
   {
      SMIMESignedGenerator gen = new SMIMESignedGenerator();
      SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
      gen.addSignerInfoGenerator(signer);

      MimeMultipart mp = gen.generate(createMsg());
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);
      String contentType = mp.getContentType();
      contentType = contentType.replace("\r\n", "").replace("\t", " ");
      System.out.println(contentType);
      String s = new String(os.toByteArray());
      StringBuilder builder = new StringBuilder();
      builder.append("Content-Type: ").append(contentType).append("\r\n\r\n").append(s);
      String output = builder.toString();

      FileOutputStream fp = new FileOutputStream("smime_signed.txt");
      fp.write(output.getBytes());
      fp.close();


   }

   @Test
   public void testPythonVerifiedBad() throws Exception
   {
      SMIMESignedGenerator gen = new SMIMESignedGenerator();
      SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", badKey, cert);
      gen.addSignerInfoGenerator(signer);

      MimeMultipart mp = gen.generate(createMsg());
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      mp.writeTo(os);
      String contentType = mp.getContentType();
      contentType = contentType.replace("\r\n", "").replace("\t", " ");
      System.out.println(contentType);
      String s = new String(os.toByteArray());
      StringBuilder builder = new StringBuilder();
      builder.append("Content-Type: ").append(contentType).append("\r\n\r\n").append(s);
      String output = builder.toString();

      FileOutputStream fp = new FileOutputStream("smime_signed_bad.txt");
      fp.write(output.getBytes());
      fp.close();


   }


}
