package org.jboss.resteasy.plugins.server.vertx;

import java.io.IOException;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.vertx.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.ResteasyDeployment;

import io.netty.buffer.ByteBufInputStream;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class VertxRequestHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    protected final RequestDispatcher dispatcher;
    private final String servletMappingPrefix;

    public VertxRequestHandler(final Vertx vertx, final RequestDispatcher dispatcher, final String servletMappingPrefix) {
        this.vertx = vertx;
        this.dispatcher = dispatcher;
        this.servletMappingPrefix = servletMappingPrefix;
    }

    public VertxRequestHandler(final Vertx vertx, final ResteasyDeployment deployment, final String servletMappingPrefix,
            final SecurityDomain domain) {
        this(vertx, new RequestDispatcher((SynchronousDispatcher) deployment.getDispatcher(), deployment.getProviderFactory(),
                domain), servletMappingPrefix);
    }

    public VertxRequestHandler(final Vertx vertx, final ResteasyDeployment deployment, final String servletMappingPrefix) {
        this(vertx, deployment, servletMappingPrefix, null);
    }

    public VertxRequestHandler(final Vertx vertx, final ResteasyDeployment deployment) {
        this(vertx, deployment, "");
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.bodyHandler(buff -> {
            Context ctx = vertx.getOrCreateContext();
            ResteasyUriInfo uriInfo = VertxUtil.extractUriInfo(request, servletMappingPrefix);
            HttpServerResponse response = request.response();
            VertxHttpResponse vertxResponse = new VertxHttpResponse(response, dispatcher.getProviderFactory(),
                    request.method());
            VertxHttpRequest vertxRequest = new VertxHttpRequest(ctx, request, uriInfo, dispatcher.getDispatcher(),
                    vertxResponse, false);
            if (buff.length() > 0) {
                ByteBufInputStream in = new ByteBufInputStream(buff.getByteBuf());
                vertxRequest.setInputStream(in);
            }

            try {
                dispatcher.service(ctx, request, response, vertxRequest, vertxResponse, true);
            } catch (Failure e1) {
                vertxResponse.setStatus(e1.getErrorCode());
            } catch (Exception ex) {
                vertxResponse.setStatus(500);
                LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), ex);
            }

            if (!vertxRequest.getAsyncContext().isSuspended()) {
                try {
                    vertxResponse.finish();
                } catch (IOException e) {
                    LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), e);
                }
            }
        });
    }
}
