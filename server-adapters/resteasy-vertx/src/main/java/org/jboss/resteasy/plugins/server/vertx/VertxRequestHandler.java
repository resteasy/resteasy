package org.jboss.resteasy.plugins.server.vertx;

import io.netty.buffer.ByteBufInputStream;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.vertx.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import java.io.IOException;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VertxRequestHandler implements Handler<HttpServerRequest>
{

   private final Vertx vertx;
   protected final RequestDispatcher dispatcher;
   private final String servletMappingPrefix;

   public VertxRequestHandler(Vertx vertx, ResteasyDeployment deployment, String servletMappingPrefix, SecurityDomain domain)
   {
      this.vertx = vertx;
      this.dispatcher = new RequestDispatcher((SynchronousDispatcher) deployment.getDispatcher(), deployment.getProviderFactory(), domain);
      this.servletMappingPrefix = servletMappingPrefix;
   }

   public VertxRequestHandler(Vertx vertx, ResteasyDeployment deployment, String servletMappingPrefix)
   {
      this(vertx, deployment, servletMappingPrefix, null);
   }

   public VertxRequestHandler(Vertx vertx, ResteasyDeployment deployment)
   {
      this(vertx, deployment, "");
   }

   @Override
   public void handle(HttpServerRequest request)
   {
      request.bodyHandler(buff -> {
         Context ctx = vertx.getOrCreateContext();
         ResteasyUriInfo uriInfo = VertxUtil.extractUriInfo(request, servletMappingPrefix);
         ResteasyHttpHeaders headers = VertxUtil.extractHttpHeaders(request);
         HttpServerResponse response = request.response();
         VertxHttpResponse vertxResponse = new VertxHttpResponse(response, dispatcher.getProviderFactory(), request.method());
         VertxHttpRequest vertxRequest = new VertxHttpRequest(ctx, headers, uriInfo, request.rawMethod(), dispatcher.getDispatcher(), vertxResponse, false);
         if (buff.length() > 0)
         {
            ByteBufInputStream in = new ByteBufInputStream(buff.getByteBuf());
            vertxRequest.setInputStream(in);
         }

         try
         {
            dispatcher.service(ctx, request, response, vertxRequest, vertxResponse, true);
         } catch (Failure e1)
         {
            vertxResponse.setStatus(e1.getErrorCode());
         } catch (Exception ex)
         {
            vertxResponse.setStatus(500);
            LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), ex);
         }

         if (!vertxRequest.getAsyncContext().isSuspended())
         {
            try
            {
               vertxResponse.finish();
            } catch (IOException e)
            {
               LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), e);
            }
         }
      });
   }
}
