package org.jboss.resteasy.plugins.server.netty.cdi;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;

import javax.enterprise.context.Dependent;

/**
 * A CDI aware Netty Jaxrs Server.
 */
@Dependent
public class CdiNettyJaxrsServer extends NettyJaxrsServer {
    @Override
    protected RequestDispatcher createRequestDispatcher() {
        return new CdiRequestDispatcher((SynchronousDispatcher)super.deployment.getDispatcher(),
                                super.deployment.getProviderFactory(), super.domain);
    }
}
