package dev.resteasy.embedded.server;

import java.util.Map;

import jakarta.annotation.Priority;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.PriorityServiceLoader;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.ContainerInstance;
import org.jboss.weld.environment.servlet.Container;
import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.environment.undertow.UndertowContainer;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * An {@link EmbeddedServer} that uses Undertow and enables CDI support.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
@Priority(100)
public class UndertowCdiEmbeddedServer implements EmbeddedServer {
    private final ServletContainer servletContainer;
    private final PathHandler rootHandler;
    private final CdiResteasyDeployment deployment;
    private volatile Undertow server;
    private volatile Runnable undeployAction;

    public UndertowCdiEmbeddedServer() {
        servletContainer = ServletContainer.Factory.newInstance();
        rootHandler = new PathHandler();
        deployment = new CdiResteasyDeployment();
    }

    @Override
    public void start(final Configuration configuration) {
        final Undertow.Builder builder = Undertow.builder()
                .setHandler(rootHandler);
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
                return;
            case MANDATORY:
                builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, SslClientAuthMode.REQUIRED);
                break;
        }
        PriorityServiceLoader<UndertowBuilderConfigurator> undertowBuilderConfigurators = PriorityServiceLoader.load(
                UndertowBuilderConfigurator.class);
        for (UndertowBuilderConfigurator undertowBuilderConfigurator : undertowBuilderConfigurators) {
            undertowBuilderConfigurator.configure(builder);
        }
        final Undertow server = builder.build();
        server.start();
        this.server = server;
        // Deploy to Undertow
        final DeploymentInfo deploymentInfo = deploymentInfo(configuration);
        final DeploymentManager manager = servletContainer.addDeployment(deploymentInfo);
        manager.deploy();
        try {
            rootHandler.addPrefixPath(deploymentInfo.getContextPath(), manager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        undeployAction = () -> {
            try {
                manager.stop();
            } catch (ServletException e) {
                LogMessages.LOGGER.failedToStopDeploymentManager(e, deploymentInfo.getDeploymentName());
            }
            manager.undeploy();
            servletContainer.removeDeployment(deploymentInfo);
        };
    }

    @Override
    public void stop() {
        final ResteasyDeployment deployment = this.deployment;
        if (deployment != null) {
            deployment.stop();
        }
        final Runnable undeployAction = this.undeployAction;
        if (undeployAction != null) {
            undeployAction.run();
        }
        final Undertow server = this.server;
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public ResteasyDeployment getDeployment() {
        return deployment;
    }

    private DeploymentInfo deploymentInfo(final Configuration configuration) {
        DeploymentInfo deploymentInfo;
        if (configuration.hasProperty(UndertowConfigurationOptions.DEPLOYMENT_INFO)) {
            final Object deployment = configuration.property(UndertowConfigurationOptions.DEPLOYMENT_INFO);
            if (deployment instanceof DeploymentInfo) {
                deploymentInfo = ((DeploymentInfo) deployment);
            } else {
                LogMessages.LOGGER.invalidProperty(UndertowConfigurationOptions.DEPLOYMENT_INFO,
                        deployment.getClass().getName(), DeploymentInfo.class.getName());
                deploymentInfo = new DeploymentInfo();
            }
        } else {
            deploymentInfo = new DeploymentInfo();
        }
        return configure(deploymentInfo, configuration);
    }

    @SuppressWarnings("unchecked")
    private DeploymentInfo configure(final DeploymentInfo deploymentInfo, final Configuration configuration) {
        final ContainerInstance container = deployment.getContainer();

        // Ensure the RESTEasy deployment is started
        EmbeddedServers.validateDeployment(deployment);

        // Determine the servlet mapping name
        String mapping = EmbeddedServers.checkContextPath(deployment);
        if (!mapping.endsWith("/")) {
            mapping += "/";
        }
        mapping = mapping + "*";

        // Configure the RESTEasy Servlet
        final ServletInfo resteasyServlet;
        if (deploymentInfo.getServlets().containsKey("ResteasyServlet")) {
            resteasyServlet = deploymentInfo.getServlets().get("ResteasyServlet");
        } else {
            resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                    .setAsyncSupported(true)
                    .setLoadOnStartup(1)
                    .addMapping(mapping);
        }

        if (!"/*".equals(mapping)) {
            // Configure the mapping prefix for RESTEasy
            final String prefix = mapping.substring(0, mapping.length() - 2);
            resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);
        }

        // Check for context parameters
        if (configuration.hasProperty(UndertowConfigurationOptions.CONTEXT_PARAMETERS)) {
            final Object value = configuration.property(UndertowConfigurationOptions.CONTEXT_PARAMETERS);
            if (value instanceof Map) {
                for (Map.Entry<String, String> e : ((Map<String, String>) value).entrySet()) {
                    deploymentInfo.addInitParameter(e.getKey(), e.getValue());
                }
            } else {
                LogMessages.LOGGER.invalidProperty(UndertowConfigurationOptions.CONTEXT_PARAMETERS, value.getClass()
                        .getName(), Map.class.getName());
            }
        }
        // Ensure the Undertow Weld Container is always the one chosen.
        deploymentInfo.addInitParameter(Container.CONTEXT_PARAM_CONTAINER_CLASS, UndertowContainer.class.getName());
        // Determine the context path
        final String contextPath = EmbeddedServers.checkContextPath(configuration.rootPath());

        if (deploymentInfo.getDefaultMultipartConfig() == null) {
            final Application application = deployment.getApplication();
            final MultipartConfig multipartConfig = application.getClass().getAnnotation(MultipartConfig.class);
            if (multipartConfig != null) {
                deploymentInfo.setDefaultMultipartConfig(new MultipartConfigElement(multipartConfig));
            }
        }
        return deploymentInfo
                // Set up deployment specific info
                .setClassLoader(deployment.getApplication().getClass().getClassLoader())
                .setContextPath(contextPath)
                .setDeploymentName("RESTEasy-" + contextPath)
                // Set up the RESTEasy Servlet
                .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
                .addServlet(resteasyServlet)
                // Configure the Weld listener
                .addListener(Servlets.listener(Listener.class, new ImmediateInstanceFactory<>(Listener.using(container))));
    }
}
