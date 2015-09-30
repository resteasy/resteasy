package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.server.sun.http.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.sun.http.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyHttpHandler implements HttpHandler
{
   protected Dispatcher dispatcher;
   protected ResteasyProviderFactory providerFactory;

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   @Override
   public void handle(final HttpExchange httpExchange) throws IOException
   {
      HttpServerResponse response = new HttpServerResponse(providerFactory, httpExchange);
      HttpRequest request = null;
      try
      {
         request = new HttpServerRequest((SynchronousDispatcher)dispatcher, response, httpExchange);
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.trace(Messages.MESSAGES.errorParsingRequest(), e);
         httpExchange.sendResponseHeaders(400, -1);
         return;
      }

      try
      {
         //logger.info("***PATH: " + request.getRequestURL());
         // classloader/deployment aware RestasyProviderFactory.  Used to have request specific
         // ResteasyProviderFactory.getInstance()
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.push(providerFactory);
         }


         try
         {
            ResteasyProviderFactory.pushContext(HttpExchange.class, httpExchange);
            ResteasyProviderFactory.pushContext(HttpContext.class, httpExchange.getHttpContext());
            dispatcher.invoke(request, response);
            if (!response.isCommitted())
            {
               response.commitHeaders();
            }
         }
         catch (Exception ex)
         {
            LogMessages.LOGGER.error(Messages.MESSAGES.wtf(), ex);
            if (!response.isCommitted())
            {
               httpExchange.sendResponseHeaders(500, -1);
            }
         }
         finally
         {
            ResteasyProviderFactory.clearContextData();
            httpExchange.getResponseBody().close();
         }
      }
      finally
      {
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.pop();
         }

      }


   }
}
