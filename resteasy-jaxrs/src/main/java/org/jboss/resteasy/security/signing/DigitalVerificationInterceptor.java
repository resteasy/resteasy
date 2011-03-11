package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.InputStreamToByteArray;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ClientInterceptor
@ServerInterceptor
@DecoderPrecedence
public class DigitalVerificationInterceptor implements MessageBodyReaderInterceptor
{
   @Override
   public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException
   {
      Verifier verifier = (Verifier) context.getAttribute(Verifier.class.getName());
      if (verifier == null)
      {
         return context.proceed();
      }

      //System.out.println("TRACE: found verifier");

      MultivaluedMap<String, String> headers = context.getHeaders();
      ContentSignatures contentSignatures = new ContentSignatures();
      List<String> strings = headers.get(ContentSignature.CONTENT_SIGNATURE);
      for (String headerVal : strings)
      {
         contentSignatures.addSignature(headerVal);
      }

      InputStream old = context.getInputStream();
      try
      {
         InputStreamToByteArray stream = new InputStreamToByteArray(old);
         context.setInputStream(stream);
         Object rtn = context.proceed();
         byte[] body = stream.toByteArray();

         if (verifier.getRepository() == null)
         {
            KeyRepository repository = (KeyRepository) context.getAttribute(KeyRepository.class.getName());
            if (repository == null)
            {
               repository = ResteasyProviderFactory.getContextData(KeyRepository.class);
            }
            verifier.setRepository(repository);
         }

         VerificationResults results = verifier.verify(contentSignatures, headers, body);
         if (results.isVerified() == false)
         {
            throw new UnauthorizedSignatureException(results);
         }
         return rtn;
      }
      finally
      {
         context.setInputStream(old);
      }
   }
}
