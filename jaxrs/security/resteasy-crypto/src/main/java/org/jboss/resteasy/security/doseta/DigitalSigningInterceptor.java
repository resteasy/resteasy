package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

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
public class DigitalSigningInterceptor implements MessageBodyWriterInterceptor, ClientExecutionInterceptor, PostProcessInterceptor
{

   protected List<DKIMSignature> getHeaders(MultivaluedMap<String, Object> headers)
   {
      List<DKIMSignature> list = new ArrayList<DKIMSignature>();

      List<Object> signatures = headers.get(DKIMSignature.DKIM_SIGNATURE);
      if (signatures == null || signatures.isEmpty())
      {
         return list;
      }

      for (Object obj : signatures)
      {
         if (obj instanceof DKIMSignature)
         {
            list.add((DKIMSignature) obj);
         }
      }
      return list;
   }

   @Override
   public ClientResponse execute(ClientExecutionContext context) throws Exception
   {
      if (context.getRequest().getBody() != null)
      {
         return context.proceed(); // let the MBW handle this
      }

      MultivaluedMap<String, Object> headers = context.getRequest().getHeadersAsObjects();
      List<DKIMSignature> list = getHeaders(headers);

      for (DKIMSignature dosetaSignature : list)
      {
         KeyRepository repository = (KeyRepository) context.getRequest().getAttributes().get(KeyRepository.class.getName());
         sign(repository, headers, null, dosetaSignature);
      }

      return context.proceed();
   }

   @Override
   public void postProcess(ServerResponse response)
   {
      if (response.getEntity() != null)
      {
         return;  // let the MBW handle this
      }

      MultivaluedMap<String, Object> headers = response.getMetadata();
      List<DKIMSignature> list = getHeaders(headers);

      for (DKIMSignature dosetaSignature : list)
      {
         try
         {
            sign(null, headers, null, dosetaSignature);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      MultivaluedMap<String, Object> headers = context.getHeaders();

      List<DKIMSignature> list = getHeaders(headers);

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

         for (DKIMSignature dosetaSignature : list)
         {
            KeyRepository repository = (KeyRepository) context.getAttribute(KeyRepository.class.getName());
            sign(repository, headers, body, dosetaSignature);
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

   protected void sign(KeyRepository repository, MultivaluedMap<String, Object> headers, byte[] body, DKIMSignature dosetaSignature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
   {
      // if its already signed, don't bother
      if (dosetaSignature.getBased64Signature() != null) return;

      PrivateKey privateKey = dosetaSignature.getPrivateKey();
      if (privateKey == null)
      {
         if (repository == null) repository = ResteasyProviderFactory.getContextData(KeyRepository.class);

         if (repository == null)
         {
            throw new RuntimeException("Unable to locate a private key to sign message, repository is null.");

         }

         privateKey = repository.findPrivateKey(dosetaSignature);
         if (privateKey == null)
         {
            throw new RuntimeException("Unable to find key to sign message. Repository returned null. ");
         }
      }
      dosetaSignature.sign(headers, body, privateKey);
   }
}
