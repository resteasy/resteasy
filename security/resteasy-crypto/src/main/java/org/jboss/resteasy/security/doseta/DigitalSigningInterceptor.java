package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.security.doseta.i18n.*;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

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
@Priority(Priorities.ENTITY_CODER)
public class DigitalSigningInterceptor implements WriterInterceptor, ContainerResponseFilter, ClientRequestFilter
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
