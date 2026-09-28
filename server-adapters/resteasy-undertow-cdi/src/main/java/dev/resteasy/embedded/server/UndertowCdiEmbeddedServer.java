package dev.resteasy.embedded.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.annotation.Priority;
import jakarta.servlet.ServletException;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.SeBootstrap.Configuration;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.spi.PriorityServiceLoader;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;

/**
 * An {@link EmbeddedServer} that uses Undertow and enables CDI support.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
@Priority(100)
public class UndertowCdiEmbeddedServer implements EmbeddedServer {
    private final Lock lock = new ReentrantLock();
    private final ServletContainer servletContainer;
    private final PathHandler rootHandler;
    private final CdiResteasyDeployment deployment;
    private Undertow server;
    private Runnable undeployAction;

    public UndertowCdiEmbeddedServer() {
        this(null);
    }

    /**
     * Creates a new embedded server with an optional configuration.
     *
     * @param configuration the configuration to use to start the server, may be {@code null}
     * @since 6.2.17
     */
    public UndertowCdiEmbeddedServer(final Configuration configuration) {
        servletContainer = ServletContainer.Factory.newInstance();
        rootHandler = new PathHandler();
        deployment = new CdiResteasyDeployment(
                configuration == null ? SeBootstrap.Configuration.builder().build() : configuration);
    }

    @Override
    public void start(final Configuration configuration) {
        lock.lock();
        try {
            if (server != null) {
                // Ignore since the server is already started
                return;
            }
            final Undertow.Builder builder = Undertow.builder()
                    .setHandler(rootHandler)
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true);
            if ("HTTPS".equalsIgnoreCase(configuration.protocol())) {
                builder.addHttpsListener(configuration.port(), configuration.host(), configuration.sslContext());
            } else {
                builder.addHttpListener(configuration.port(), configuration.host());
            }
            switch (configuration.sslClientAuthentication()) {
                case NONE:
                    builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, SslClientAuthMode.NOT_REQUESTED);
                    break;
                case OPTIONAL:
                    builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, SslClientAuthMode.REQUESTED);
                    break;
                case MANDATORY:
                    builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, SslClientAuthMode.REQUIRED);
                    break;
            }

            // Check for configurators and allow them to configure the server before starting
            final PriorityServiceLoader<UndertowBuilderConfigurator> undertowBuilderConfigurators = PriorityServiceLoader.load(
                    UndertowBuilderConfigurator.class);
            for (UndertowBuilderConfigurator undertowBuilderConfigurator : undertowBuilderConfigurators) {
                undertowBuilderConfigurator.configure(builder);
            }

            server = builder.build();
            server.start();

            // Ensure the RESTEasy deployment is started, otherwise the deploymentInfo() may return null
            EmbeddedServers.validateDeployment(deployment);
            // Deploy to Undertow
            final DeploymentInfo deploymentInfo = deployment.deploymentInfo();
            final DeploymentManager manager = servletContainer.addDeployment(deploymentInfo);
            manager.deploy();
            rootHandler.addPrefixPath(deploymentInfo.getContextPath(), manager.start());
            undeployAction = () -> {
                try {
                    manager.stop();
                } catch (ServletException e) {
                    LogMessages.LOGGER.failedToStopDeploymentManager(e, deploymentInfo.getDeploymentName());
                }
                manager.undeploy();
                servletContainer.removeDeployment(deploymentInfo);
            };
        } catch (Exception e) {
            RuntimeException exception;
            if (e instanceof RuntimeException) {
                exception = (RuntimeException) e;
            } else {
                exception = new RuntimeException(e);
            }
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception ex) {
                    exception.addSuppressed(ex);
                }
            }
            try {
                deployment.stop();
            } catch (Exception ex) {
                exception.addSuppressed(ex);
            }
            server = null;
            throw exception;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            deployment.stop();
            if (undeployAction != null) {
                undeployAction.run();
            }
            if (server != null) {
                server.stop();
            }
        } finally {
            undeployAction = null;
            server = null;
            lock.unlock();
        }
    }

    @Override
    public ResteasyDeployment getDeployment() {
        return deployment;
    }
}
