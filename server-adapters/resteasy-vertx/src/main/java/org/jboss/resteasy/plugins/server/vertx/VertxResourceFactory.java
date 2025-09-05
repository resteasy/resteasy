package org.jboss.resteasy.plugins.server.vertx;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class VertxResourceFactory implements ResourceFactory {

    private final ResourceFactory delegate;
    private final String id = UUID.randomUUID().toString();

    public VertxResourceFactory(final ResourceFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<?> getScannableClass() {
        return delegate.getScannableClass();
    }

    @Override
    public void registered(ResteasyProviderFactory factory) {
        delegate.registered(factory);
    }

    @Override
    public Object createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory) {
        Context ctx = Vertx.currentContext();
        if (ctx != null) {
            Object resource = ctx.get(id);
            if (resource == null) {
                Object createdResource = delegate.createResource(request, response, factory);
                if (createdResource instanceof CompletionStage) {
                    CompletionStage<Object> stage = (CompletionStage<Object>) createdResource;
                    return stage.thenApply(newResource -> {
                        ctx.put(id, newResource);
                        return newResource;
                    });

                } else {
                    return createdResource;
                }
            }
            return resource;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void requestFinished(HttpRequest request, HttpResponse response, Object resource) {
        delegate.requestFinished(request, response, resource);
    }

    @Override
    public void unregistered() {
        delegate.unregistered();
    }
}
