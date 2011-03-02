package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ServerInterceptor
@ClientInterceptor
@DecoderPrecedence
public class DigitalSigningInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      MultivaluedMap<String, Object> headers = context.getHeaders();
      if (!headers.containsKey("Content-Signature"))
      {
         context.proceed();
         return;
      }


      List<Object> signatures = headers.get("Content-Signature");
      if (signatures == null || signatures.size() == 0)
      {
         context.proceed();
         return;
      }

      ContentSignatures contentSignatures = null;
      for (Object obj : signatures)
      {
         if (obj instanceof ContentSignature)
         {
            throw new IllegalArgumentException("It is illegal to use ContentSignature as a header value");
         }
         else if (obj instanceof ContentSignatures)
         {
            if (contentSignatures != null)
            {
               throw new IllegalArgumentException("It is illegal to have more than one ContentSignatures object as a header value");
            }
            contentSignatures = (ContentSignatures) obj;
         }
      }

      if (contentSignatures == null)
      {
         context.proceed();
         return;
      }

      //System.out.println("TRACE: Found ContentSignatures");
      OutputStream old = context.getOutputStream();
      try
      {
         // store body in a byte array so we can use it to calculate signature
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         context.setOutputStream(baos);
         context.proceed();
         byte[] body = baos.toByteArray();

         for (ContentSignature contentSignature : contentSignatures.getSignatures())
         {
            sign(context, headers, body, contentSignature, contentSignatures);
         }
         headers.putSingle("Content-Signature", contentSignatures);

         old.write(body);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to sign", e);
      }
      finally
      {
         context.setOutputStream(old);
      }
   }

   protected void sign(MessageBodyWriterContext context, MultivaluedMap<String, Object> headers, byte[] body, ContentSignature contentSignature, ContentSignatures contentSignatures) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
   {
      // if its already signed, don't bother
      if (contentSignature.getHexSignature() != null) return;

      PrivateKey privateKey = contentSignature.getPrivateKey();
      if (privateKey == null)
      {
         KeyRepository repository = (KeyRepository) context.getAttribute(KeyRepository.class.getName());
         if (repository == null)
         {
            repository = ResteasyProviderFactory.getContextData(KeyRepository.class);
         }

         if (repository == null)
         {
            throw new RuntimeException("Unable to locate a public key to sign message.");

         }

         String keyAlias = null;
         keyAlias = contentSignature.getKeyAlias();
         if (keyAlias == null) keyAlias = contentSignature.getSigner();
         if (keyAlias == null) keyAlias = ContentSignature.DEFAULT_SIGNER;

         privateKey = repository.getPrivateKey(keyAlias);
         if (privateKey == null)
         {
            throw new RuntimeException("Unable to find key to sign message for key alias: " + keyAlias);
         }
      }
      contentSignature.sign(headers, body, contentSignatures, privateKey);
   }

}
