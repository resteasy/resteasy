package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Servlet3AsyncHttpRequest extends HttpServletInputMessage
{
   protected HttpServletResponse response;

   public Servlet3AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse response, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
   {
      super(httpServletRequest, httpResponse, httpHeaders, uriInfo, s, synchronousDispatcher);
      this.response = response;
   }

   @Override
   public void initialRequestThreadFinished()
   {
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long l)
   {
      suspended = true;
      final AsyncContext context = request.startAsync(request, response);
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
               context.complete();
            }
         }
      };
      return asynchronousResponse;
   }

}
