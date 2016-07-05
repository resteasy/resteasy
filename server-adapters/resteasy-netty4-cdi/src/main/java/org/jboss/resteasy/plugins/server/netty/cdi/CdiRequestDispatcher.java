package org.jboss.resteasy.plugins.server.netty.cdi;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.weld.context.bound.BoundRequestContext;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.CDI;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A request dispatcher that starts a RequestContext during invocation.
 */
@Vetoed
public class CdiRequestDispatcher extends RequestDispatcher {
    public CdiRequestDispatcher(SynchronousDispatcher dispatcher, ResteasyProviderFactory providerFactory, SecurityDomain domain) {
        super(dispatcher, providerFactory, domain);
    }

    @Override
    public void service(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response, boolean handleNotFound) throws IOException {
        BoundRequestContext context = CDI.current().select(BoundRequestContext.class).get();
        Map<String,Object> contextMap = new HashMap<String,Object>();
        context.associate(contextMap);
        context.activate();
        try
        {
            super.service(ctx, request,response,handleNotFound);
        }
        finally
        {
            context.invalidate();
            context.deactivate();
            context.dissociate(contextMap);
        }
    }
}
