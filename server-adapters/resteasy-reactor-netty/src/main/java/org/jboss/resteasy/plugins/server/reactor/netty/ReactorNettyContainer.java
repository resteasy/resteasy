package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.PortProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactorNettyContainer {

    private static final Logger log = LoggerFactory.getLogger(ReactorNettyContainer.class);

    public static ReactorNettyJaxrsServer reactorNettyJaxrsServer;

    public static ResteasyDeployment start() throws Exception
    {
        return start("");
    }

    public static ResteasyDeployment start(String bindPath) throws Exception
    {
        return start(bindPath, null);
    }

    public static void start(ResteasyDeployment deployment)
    {
        reactorNettyJaxrsServer = new ReactorNettyJaxrsServer();
        reactorNettyJaxrsServer.setDeployment(deployment);
        reactorNettyJaxrsServer.setPort(PortProvider.getPort());
        reactorNettyJaxrsServer.setRootResourcePath("");
        reactorNettyJaxrsServer.setSecurityDomain(null);
        reactorNettyJaxrsServer.start();
    }

    public static ResteasyDeployment start(ReactorNettyJaxrsServer server)
    {
        final ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        reactorNettyJaxrsServer = server;
        reactorNettyJaxrsServer.setDeployment(deployment);
        reactorNettyJaxrsServer.start();
        return reactorNettyJaxrsServer.getDeployment();
    }

    public static ResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
    {
        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setSecurityEnabled(true);
        return start(bindPath, domain, deployment);
    }

    public static ResteasyDeployment start(
            String bindPath,
            SecurityDomain domain,
            ResteasyDeployment deployment) throws Exception
    {
        reactorNettyJaxrsServer = new ReactorNettyJaxrsServer();
        reactorNettyJaxrsServer.setDeployment(deployment);
        reactorNettyJaxrsServer.setPort(PortProvider.getPort());
        reactorNettyJaxrsServer.setRootResourcePath(bindPath);
        reactorNettyJaxrsServer.setSecurityDomain(domain);
        reactorNettyJaxrsServer.start();
        return reactorNettyJaxrsServer.getDeployment();
    }

    public static void stop()
    {
        if (reactorNettyJaxrsServer != null)
        {
            try
            {
                reactorNettyJaxrsServer.stop();
            }
            catch (Exception e)
            {
                log.error("Failed to stop the server", e);
            }
        }
        reactorNettyJaxrsServer = null;
    }

    public static void main(String[] args) throws Exception {
        start();
    }
}
