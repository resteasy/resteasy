package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Servlet3AsyncHttpRequest extends HttpServletInputMessage
{
   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, InputStream inputStream, UriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, httpResponse, httpHeaders, inputStream, uriInfo, s, synchronousDispatcher);
   }

   @Override
   public void initialRequestThreadFinished()
   {
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long l)
   {
      request.suspend(l);
      return new AsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               dispatcher.asynchronousDelivery(Servlet3AsyncHttpRequest.this, httpResponse, response);
            }
            finally
            {
               try
               {
                  request.complete();
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      };
   }

   @Override
   public boolean isInitial()
   {
      return request.isInitial();
   }

   @Override
   public boolean isSuspended()
   {
      return request.isSuspended();
   }

   @Override
   public boolean isTimeout()
   {
      return request.isTimeout();
   }
}
