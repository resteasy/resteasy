package org.jboss.resteasy.security.smime;

import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.WriterException;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("text/plain")
public class PKCS7SignatureTextWriter implements AsyncMessageBodyWriter<SignedOutput>
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
         os.write(Base64.getEncoder().encodeToString(encoded).getBytes(StandardCharsets.UTF_8));
      }
      catch (Exception e)
      {
         throw new WriterException(e);
      }
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(SignedOutput out, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream) {
       try
       {
          byte[] encoded = PKCS7SignatureWriter.sign(providers, out);
          return entityStream.asyncWrite(Base64.getEncoder().encodeToString(encoded).getBytes(StandardCharsets.UTF_8));
       }
       catch (Exception e)
       {
          return ProviderHelper.completedException(new WriterException(e));
       }
   }
}
