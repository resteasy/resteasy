package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.apache.catalina.CometEvent;

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
public class Tomcat6AsyncHttpRequest extends HttpServletInputMessage
{
   protected CometEvent event;

   public Tomcat6AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, InputStream inputStream, UriInfo uriInfo, String httpMethodName, SynchronousDispatcher synchronousDispatcher, CometEvent event)
   {
      super(httpServletRequest, httpResponse, httpHeaders, inputStream, uriInfo, httpMethodName, synchronousDispatcher);
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
      this.request.setAttribute("org.apache.tomcat.comet.timeout", new Integer((int)l));
      return new AsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               dispatcher.asynchronousDelivery(Tomcat6AsyncHttpRequest.this, httpResponse, response);
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
   }
}
