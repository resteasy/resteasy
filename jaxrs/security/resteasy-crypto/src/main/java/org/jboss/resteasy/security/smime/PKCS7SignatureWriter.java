package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.WriterException;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("application/pkcs7-signature")
public class PKCS7SignatureWriter implements MessageBodyWriter<SignedOutput>
{
   static
   {
      BouncyIntegration.init();
   }

   @Context
   protected Providers providers;

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return SignedOutput.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(SignedOutput smimeOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(SignedOutput out, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException
   {
      try
      {
         byte[] encoded = sign(providers, out);
         headers.putSingle("Content-Type", "application/pkcs7-signature;micalg=\"sha1\"");
         os.write(encoded);

      }
      catch (Exception e)
      {
         throw new WriterException(e);
      }
   }

   public static byte[] sign(Providers providers, SignedOutput out) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, CMSException
   {
      ByteArrayOutputStream bodyOs = new ByteArrayOutputStream();
      MessageBodyWriter writer = providers.getMessageBodyWriter(out.getType(), out.getGenericType(), null, out.getMediaType());
      if (writer == null)
      {
         throw new WriterException("Failed to find writer for type: " + out.getType().getName());
      }
      MultivaluedMapImpl<String, Object> bodyHeaders = new MultivaluedMapImpl<String, Object>();
      bodyHeaders.add("Content-Type",  out.getMediaType().toString());
      writer.writeTo(out.getEntity(), out.getType(), out.getGenericType(), null, out.getMediaType(), bodyHeaders, bodyOs);
      CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
      signGen.addSigner(out.getPrivateKey(), (X509Certificate)out.getCertificate(), CMSSignedDataGenerator.DIGEST_SHA1);
      //signGen.addCertificatesAndCRLs(certs);
      CMSProcessable content = new CMSProcessableByteArray(bodyOs.toByteArray());

      CMSSignedData signedData = signGen.generate(content, true, "BC");
      return signedData.getEncoded();
   }
}
