package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.crypto.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.InputStreamToByteArray;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
      List<String> strings = headers.get(DKIMSignature.DKIM_SIGNATURE);
      if (strings == null)
      {
         throw new UnauthorizedSignatureException(Messages.MESSAGES.thereWasNoSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
      }
      List<DKIMSignature> signatures = new ArrayList<DKIMSignature>();
      for (String headerVal : strings)
      {
         try
         {
            signatures.add(new DKIMSignature(headerVal));
         }
         catch (Exception e)
         {
            throw new UnauthorizedSignatureException(Messages.MESSAGES.malformedSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
         }
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

         VerificationResults results = verifier.verify(signatures, headers, body);
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
