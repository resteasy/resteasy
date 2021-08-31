package org.jboss.resteasy.security.doseta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.doseta.i18n.LogMessages;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.jboss.resteasy.spi.BlockingAsyncOutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class DigitalSigningInterceptor implements AsyncWriterInterceptor, ContainerResponseFilter, ClientRequestFilter
{

   protected List<DKIMSignature> getHeaders(MultivaluedMap<String, Object> headers)
   {

      List<Object> signatures = headers.get(DKIMSignature.DKIM_SIGNATURE);
      if (signatures == null || signatures.isEmpty())
      {
         return Collections.EMPTY_LIST;
      }
      List<DKIMSignature> list = new ArrayList<DKIMSignature>();

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
   public void filter(ClientRequestContext requestContext) throws IOException
   {
      if (requestContext.hasEntity())
      {
         // let WriterInterceptor handle this
         return;
      }
      MultivaluedMap<String, Object> headers = requestContext.getHeaders();
      List<DKIMSignature> list = getHeaders(headers);

      for (DKIMSignature dosetaSignature : list)
      {
         KeyRepository repository = (KeyRepository) requestContext.getProperty(KeyRepository.class.getName());
         try
         {
            sign(repository, headers, null, dosetaSignature);
         }
         catch (Exception e)
         {
            throw new ProcessingException(e);
         }
      }

   }

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      if (responseContext.getEntity() != null)
      {
         return; // let WriterInterceptor handle this
      }

      MultivaluedMap<String, Object> headers = responseContext.getHeaders();
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

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());
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
            KeyRepository repository = (KeyRepository) context.getProperty(KeyRepository.class.getName());
            sign(repository, headers, body, dosetaSignature);
         }

         old.write(body);
      }
      catch (Exception e)
      {
         throw new RuntimeException(Messages.MESSAGES.failedToSign(), e);
      }
      finally
      {
         context.setOutputStream(old);
      }
   }

   @Override
   public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context)
   {
      LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());
      MultivaluedMap<String, Object> headers = context.getHeaders();

      List<DKIMSignature> list = getHeaders(headers);

      if (list.isEmpty())
      {
         return context.asyncProceed();
      }

      //System.out.println("TRACE: Found ContentSignatures");
      AsyncOutputStream old = context.getAsyncOutputStream();
      // store body in a byte array so we can use it to calculate signature
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      context.setAsyncOutputStream(new BlockingAsyncOutputStream(baos));
      return context.asyncProceed()
              .thenCompose(v -> {
                  byte[] body = baos.toByteArray();

                  try {
                      for (DKIMSignature dosetaSignature : list)
                      {
                          KeyRepository repository = (KeyRepository) context.getProperty(KeyRepository.class.getName());
                          sign(repository, headers, body, dosetaSignature);
                      }
                  } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnsupportedEncodingException e) {
                      CompletableFuture<Void> ret = new CompletableFuture<>();
                      ret.completeExceptionally(e);
                      return ret;
                  }

                  return old.asyncWrite(body);
              }).whenComplete((v, t) -> {
                  context.setAsyncOutputStream(old);
                  if(t != null)
                      throw new RuntimeException(Messages.MESSAGES.failedToSign(), t);
              });
   }

   protected void sign(KeyRepository repository, MultivaluedMap<String, Object> headers, byte[] body, DKIMSignature dosetaSignature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
   {
      // if its already signed, don't bother
      if (dosetaSignature.getBased64Signature() != null) return;

      PrivateKey privateKey = dosetaSignature.getPrivateKey();
      if (privateKey == null)
      {
         if (repository == null) repository = ResteasyContext.getContextData(KeyRepository.class);

         if (repository == null)
         {
            throw new InvalidKeyException(Messages.MESSAGES.unableToLocatePrivateKey());

         }

         privateKey = repository.findPrivateKey(dosetaSignature);
         if (privateKey == null)
         {
            throw new InvalidKeyException(Messages.MESSAGES.unableToFindKey());
         }
      }
      dosetaSignature.sign(headers, body, privateKey);
   }
}
