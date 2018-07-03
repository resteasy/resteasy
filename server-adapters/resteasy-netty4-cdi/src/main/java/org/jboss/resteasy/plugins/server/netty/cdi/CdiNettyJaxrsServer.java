package org.jboss.resteasy.plugins.server.netty.cdi;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

/**
 * A CDI aware Netty Jaxrs Server.
 */
@Dependent
public class CdiNettyJaxrsServer extends NettyJaxrsServer {
    private Instance<Object> instance;
    public CdiNettyJaxrsServer() {
       this.instance = CDI.current();
    }
    public CdiNettyJaxrsServer(Instance<Object> instance) {
       this.instance = instance;
    }
    @Override
    protected RequestDispatcher createRequestDispatcher() {
        return new CdiRequestDispatcher((SynchronousDispatcher)super.deployment.getDispatcher(),
                                super.deployment.getProviderFactory(), super.domain, instance);
    }
}
