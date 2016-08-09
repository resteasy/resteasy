package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.CMSSignedData;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("application/pkcs7-signature")
public class PKCS7SignatureReader implements MessageBodyReader<PKCS7SignatureInput>
{
   static
   {
      BouncyIntegration.init();
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return PKCS7SignatureInput.class.isAssignableFrom(type);
   }

   @SuppressWarnings(value = "unchecked")
   public PKCS7SignatureInput readFrom(Class<PKCS7SignatureInput> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream entityStream) throws IOException, WebApplicationException
   {
      Class<?> baseType = null;
      Type baseGenericType = null;

      if (genericType != null && genericType instanceof ParameterizedType)
      {
         ParameterizedType param = (ParameterizedType) genericType;
         baseGenericType = param.getActualTypeArguments()[0];
         baseType = Types.getRawType(baseGenericType);
      }
      try
      {
         CMSSignedData data = new CMSSignedData(entityStream);
         PKCS7SignatureInput input = new PKCS7SignatureInput();
         input.setType(baseType);
         input.setGenericType(baseGenericType);
         input.setAnnotations(annotations);
         input.setData(data);

         Providers providers = ResteasyProviderFactory.getContextData(Providers.class);
         input.setProviders(providers);
         return input;
      }
      catch (Exception e)
      {
         throw new ReaderException(e);
      }

   }
}
