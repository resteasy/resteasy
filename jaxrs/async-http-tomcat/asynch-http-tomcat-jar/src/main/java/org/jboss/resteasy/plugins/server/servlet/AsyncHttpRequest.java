package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.apache.catalina.CometEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncHttpRequest extends HttpServletInputMessage
{
   public AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, InputStream inputStream, UriInfo uriInfo, String s, SynchronousDispatcher synchronousDispatcher)
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
      suspended = true;
      this.request.setAttribute("org.apache.tomcat.comet.timeout", new Integer((int)l));
      return new AsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            dispatcher.asynchronousDelivery(AsyncHttpRequest.this, httpResponse, response);
         }
      };
   }
}
