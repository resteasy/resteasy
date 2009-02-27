package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Servlet3AsyncHttpRequest extends HttpServletInputMessage
{
   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, httpResponse, httpHeaders, uriInfo, s, synchronousDispatcher);
   }

   @Override
   public void initialRequestThreadFinished()
   {
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long l)
   {
      request.suspend(l);
      asynchronousResponse = new AbstractAsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               setupResponse((ServerResponse) response);
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
      return asynchronousResponse;
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
