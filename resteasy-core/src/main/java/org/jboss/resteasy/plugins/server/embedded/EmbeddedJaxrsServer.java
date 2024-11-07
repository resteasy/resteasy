package org.jboss.resteasy.plugins.server.embedded;

import jakarta.ws.rs.SeBootstrap.Configuration;

import org.jboss.resteasy.core.se.ResteasySeConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;

@Deprecated
public interface EmbeddedJaxrsServer<T extends EmbeddedServer> extends EmbeddedServer {
    T deploy();

    T start();

    @Override
    default void start(final Configuration configuration) {
        final Configuration config = ResteasySeConfiguration.from(configuration);
        setHostname(config.host());
        setPort(config.port());
        if (config.hasProperty(Configuration.ROOT_PATH)) {
            setRootResourcePath(config.rootPath());
        }
        start();
    }

    T setDeployment(ResteasyDeployment deployment);

    T setPort(int port);

    T setHostname(String hostname);

    T setRootResourcePath(String rootResourcePath);

    T setSecurityDomain(SecurityDomain sc);
}
