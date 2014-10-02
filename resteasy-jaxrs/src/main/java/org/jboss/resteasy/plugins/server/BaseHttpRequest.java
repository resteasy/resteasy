package org.jboss.resteasy.plugins.server;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.i18n.Messages;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.Encode;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper for creating HttpRequest implementations.  The async code is a fake implementation to work with
 * http engines that don't support async HTTP.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class BaseHttpRequest implements HttpRequest
{
   protected CountDownLatch latch;
   protected long suspendTimeout;
   protected SynchronousDispatcher dispatcher;
   protected boolean suspended;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected AbstractAsynchronousResponse asynchronousResponse;
   protected HttpResponse httpResponse;

   public BaseHttpRequest(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      if (getHttpHeaders().getMediaType().isCompatible(MediaType.valueOf("application/x-www-form-urlencoded")))
      {
         try
         {
            formParameters = FormUrlEncodedProvider.parseForm(getInputStream());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         throw new IllegalArgumentException(Messages.MESSAGES.requestMediaTypeNotUrlencoded());
      }
      return formParameters;
   }

   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = Encode.decode(getFormParameters());
      return decodedFormParameters;
   }

   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout)
   {
      suspended = true;
      latch = new CountDownLatch(1);
      this.suspendTimeout = suspendTimeout;
      asynchronousResponse = new AbstractAsynchronousResponse()
      {


         public void setResponse(Response response)
         {
            try
            {
               setupResponse((ServerResponse) response);
               dispatcher.asynchronousDelivery(BaseHttpRequest.this, httpResponse, response);
            }
            finally
            {
               latch.countDown();
            }
         }

         @Override
         public void setFailure(Exception ex)
         {
            try
            {
               dispatcher.asynchronousDelivery(BaseHttpRequest.this, httpResponse, ex);
            }
            finally
            {
               latch.countDown();
            }

         }
      };
      return asynchronousResponse;
   }

   public AsynchronousResponse getAsynchronousResponse()
   {
      return asynchronousResponse;
   }

   public boolean isInitial()
   {
      return true;
   }

   public boolean isSuspended()
   {
      return suspended;
   }

   public void initialRequestThreadFinished()
   {
      if (latch == null) return; // only block if createAsynchronousResponse was called.
      try
      {
         latch.await(suspendTimeout + 100, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }
}
