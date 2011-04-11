package org.jboss.resteasy.security.doseta;

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
import java.util.ArrayList;
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
      if (!headers.containsKey(DosetaSignature.DOSETA_SIGNATURE))
      {
         context.proceed();
         return;
      }


      List<Object> signatures = headers.get(DosetaSignature.DOSETA_SIGNATURE);
      if (signatures == null || signatures.isEmpty())
      {
         context.proceed();
         return;
      }

      List<DosetaSignature> list = new ArrayList<DosetaSignature>();

      for (Object obj : signatures)
      {
         if (obj instanceof DosetaSignature)
         {
            list.add((DosetaSignature) obj);
         }
      }

      if (list.isEmpty())
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

         for (DosetaSignature dosetaSignature : list)
         {
            sign(context, headers, body, dosetaSignature);
         }

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

   protected void sign(MessageBodyWriterContext context, MultivaluedMap<String, Object> headers, byte[] body, DosetaSignature dosetaSignature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
   {
      // if its already signed, don't bother
      if (dosetaSignature.getBased64Signature() != null) return;

      PrivateKey privateKey = dosetaSignature.getPrivateKey();
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
         keyAlias = dosetaSignature.getKeyAlias();
         if (keyAlias == null) keyAlias = dosetaSignature.getDomainIdentity();
         if (keyAlias == null)
         {
            throw new RuntimeException("Unable to locate a public key to sign message.");
         }

         privateKey = repository.getPrivateKey(keyAlias);
         if (privateKey == null)
         {
            throw new RuntimeException("Unable to find key to sign message for key alias: " + keyAlias);
         }
      }
      dosetaSignature.sign(headers, body, privateKey);
   }

}
