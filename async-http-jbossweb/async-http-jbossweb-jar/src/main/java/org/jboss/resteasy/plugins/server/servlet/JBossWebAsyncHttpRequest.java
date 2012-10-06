package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.servlet.http.HttpEvent;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JBossWebAsyncHttpRequest extends HttpServletInputMessage
{
   static protected Timer timer = new Timer("asyncTimer", true);
   protected HttpEvent event;

   public JBossWebAsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uriInfo, String httpMethodName, SynchronousDispatcher synchronousDispatcher, HttpEvent event)
   {
      super(httpServletRequest, httpResponse, httpHeaders, uriInfo, httpMethodName, synchronousDispatcher);
      this.event = event;
   }

   @Override
   public void initialRequestThreadFinished()
   {
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long l)
   {
      suspended = true;
      event.setTimeout((int) l);
      asynchronousResponse = new AbstractAsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               setupResponse((ServerResponse) response);
               dispatcher.asynchronousDelivery(JBossWebAsyncHttpRequest.this, httpResponse, response);
            }
            finally
            {
               try
               {
                  event.close();
               }
               catch (IOException ignored)
               {
               }
            }
         }
      };
      timer.schedule(new AsyncTimerTask(asynchronousResponse), l);
      return asynchronousResponse;
   }


   class AsyncTimerTask extends TimerTask
   {
      private AbstractAsynchronousResponse asyncResponse;

      AsyncTimerTask(AbstractAsynchronousResponse asyncResponse)
      {
         this.asyncResponse = asyncResponse;
      }

      @Override
      public void run()
      {
         try
         {
            Response response = Response.status(503).type(MediaType.TEXT_PLAIN).entity("timeout").build();
            asyncResponse.setupResponse((ServerResponse) response);
            dispatcher.asynchronousDelivery(JBossWebAsyncHttpRequest.this, httpResponse, response);
         }
         finally
         {
            try
            {
               event.close();
            }
            catch (IOException ignored)
            {
            }
         }
      } 
   }
}
