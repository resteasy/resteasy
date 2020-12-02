package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OutputEncryptor;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
public class EnvelopedWriter implements AsyncMessageBodyWriter<EnvelopedOutput>
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
      return EnvelopedOutput.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType);
   }

   @Override
   public long getSize(EnvelopedOutput smimeOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(EnvelopedOutput out, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException
   {
      try
      {
          os.write(getBytes(out, headers));
      }
      catch (Exception e)
      {
         throw new WriterException(e);
      }
   }

   private byte[] getBytes(EnvelopedOutput out, MultivaluedMap<String, Object> headers) throws CMSException, CertificateEncodingException, IOException, MessagingException {
       ByteArrayOutputStream baos = null;
       OutputStream encrypted = null;

       headers.putSingle("Content-Disposition", "attachment; filename=\"smime.p7m\"");
       headers.putSingle("Content-Type", "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"");
       headers.putSingle("Content-Transfer-Encoding", "base64");

       OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC)
               .setProvider("BC")
               .build();
       if (out.getCertificate() == null) throw new NullPointerException(Messages.MESSAGES.certificateObjectNotSet());
       JceKeyTransRecipientInfoGenerator infoGenerator = new JceKeyTransRecipientInfoGenerator(out.getCertificate());
       infoGenerator.setProvider("BC");
       CMSEnvelopedDataStreamGenerator generator = new CMSEnvelopedDataStreamGenerator();
       generator.addRecipientInfoGenerator(infoGenerator);


       MimeBodyPart _msg = createBodyPart(providers, out);

       baos = new ByteArrayOutputStream();
       encrypted = generator.open(baos, encryptor);

       _msg.writeTo(encrypted);
       encrypted.close();
       byte[] bytes = baos.toByteArray();
       String str = Base64.getMimeEncoder().encodeToString(bytes);
       return str.getBytes();
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(EnvelopedOutput out, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream) {
       try
       {
           return entityStream.asyncWrite(getBytes(out, httpHeaders));
       }
       catch (Exception e)
       {
          return ProviderHelper.completedException(new WriterException(e));
       }
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public static MimeBodyPart createBodyPart(Providers providers, SMIMEOutput out) throws IOException, MessagingException
   {
      ByteArrayOutputStream bodyOs = new ByteArrayOutputStream();
      MessageBodyWriter writer = providers.getMessageBodyWriter(out.getType(), out.getGenericType(), null, out.getMediaType());
      if (writer == null)
      {
         throw new WriterException(Messages.MESSAGES.failedToFindWriter(out.getType().getName()));
      }
      MultivaluedMapImpl<String, Object> bodyHeaders = new MultivaluedMapImpl<String, Object>();
      bodyHeaders.add("Content-Type",  out.getMediaType().toString());
      writer.writeTo(out.getEntity(), out.getType(), out.getGenericType(), null, out.getMediaType(), bodyHeaders, bodyOs);


      InternetHeaders ih = new InternetHeaders();

      for (Map.Entry<String, List<Object>> entry : bodyHeaders.entrySet())
      {
         for (Object value : entry.getValue())
         {
            ih.addHeader(entry.getKey(), value.toString());
         }
      }
      return new MimeBodyPart(ih, bodyOs.toByteArray());
   }
}
