package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.util.Base64;

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
@Produces("text/plain")
public class PKCS7SignatureTextWriter implements MessageBodyWriter<SignedOutput>
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
         byte[] encoded = PKCS7SignatureWriter.sign(providers, out);
         os.write(Base64.encodeBytes(encoded).getBytes("UTF-8"));
      }
      catch (Exception e)
      {
         throw new WriterException(e);
      }
   }

}
