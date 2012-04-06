package org.jboss.resteasy.plugins.server.servlet;

import org.apache.catalina.CometEvent;
import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Tomcat6AsyncHttpRequest extends HttpServletInputMessage
{
   protected CometEvent event;

   public Tomcat6AsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uriInfo, String httpMethodName, SynchronousDispatcher synchronousDispatcher, CometEvent event)
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
      try
      {
         event.setTimeout((int) l);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (ServletException e)
      {
         throw new RuntimeException(e);
      }
      asynchronousResponse = new AbstractAsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               setupResponse((ServerResponse) response);
               dispatcher.asynchronousDelivery(Tomcat6AsyncHttpRequest.this, httpResponse, response);
               try
               {
                  httpResponse.getOutputStream().flush();
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
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
      return asynchronousResponse;
   }
}
