package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.spi.WriterException;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/signed")
public class MultipartSignedWriter implements MessageBodyWriter<SignedOutput>
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
         SMIMESignedGenerator gen = new SMIMESignedGenerator();
         SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", out.getPrivateKey(), out.getCertificate());
         gen.addSignerInfoGenerator(signer);

         MimeMultipart mp = gen.generate(EnvelopedWriter.createBodyPart(providers, out));
         String contentType = mp.getContentType();
         contentType = contentType.replace("\r\n", "").replace("\t", " ");
         headers.putSingle("Content-Type", contentType);
         mp.writeTo(os);
      }
      catch (Exception e)
      {
         throw new WriterException(e);
      }
   }
}
