package org.jboss.resteasy.plugins.server.netty.cdi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Vetoed;
import jakarta.enterprise.inject.spi.CDI;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.weld.context.bound.BoundRequestContext;

import io.netty.channel.ChannelHandlerContext;

/**
 * A request dispatcher that starts a RequestContext during invocation.
 */
@Vetoed
public class CdiRequestDispatcher extends RequestDispatcher {
    private final Instance<Object> instance;

    public CdiRequestDispatcher(final SynchronousDispatcher dispatcher, final ResteasyProviderFactory providerFactory,
            final SecurityDomain domain) {
        this(dispatcher, providerFactory, domain, CDI.current());
    }

    public CdiRequestDispatcher(final SynchronousDispatcher dispatcher, final ResteasyProviderFactory providerFactory,
            final SecurityDomain domain, final Instance<Object> cdi) {
        super(dispatcher, providerFactory, domain);
        this.instance = cdi;
    }

    @Override
    public void service(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response, boolean handleNotFound)
            throws IOException {
        BoundRequestContext context = this.instance.select(BoundRequestContext.class).get();
        Map<String, Object> contextMap = new HashMap<String, Object>();
        context.associate(contextMap);
        context.activate();
        try {
            super.service(ctx, request, response, handleNotFound);
        } finally {
            context.invalidate();
            context.deactivate();
            context.dissociate(contextMap);
        }
    }
}
