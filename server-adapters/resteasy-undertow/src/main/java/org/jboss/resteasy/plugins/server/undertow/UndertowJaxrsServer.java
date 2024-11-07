package org.jboss.resteasy.plugins.server.undertow;

import static io.undertow.servlet.Servlets.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.Priority;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.EmbeddedServerHelper;
import org.jboss.resteasy.util.PortProvider;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;

/**
 * Wrapper around Undertow to make resteasy deployments easier
 * Each ResteasyDeployment or jaxrs Application is deployed under its own web deployment (WAR)
 *
 * You may also deploy after the server has started.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(150)
public class UndertowJaxrsServer implements EmbeddedJaxrsServer<UndertowJaxrsServer> {
    protected final PathHandler root = new PathHandler();
    protected final ServletContainer container = ServletContainer.Factory.newInstance();
    protected Undertow server;
    protected DeploymentManager manager;
    protected Map<String, String> contextParams;
    protected Map<String, String> initParams;

    private ResteasyDeployment deployment;
    private int port = PortProvider.getPort();
    private String hostname = "localhost";
    private String rootResourcePath;
    private EmbeddedServerHelper serverHelper = new EmbeddedServerHelper();

    @Override
    public UndertowJaxrsServer deploy() {
        serverHelper.checkDeployment(deployment);
        return deploy(deployment, "/",
                deployment.getClass().getClassLoader());
    }

    @Override
    public void start(final SeBootstrap.Configuration configuration) {
        setHostname(configuration.host())
                .setPort(configuration.port())
                .setRootResourcePath(configuration.rootPath());
        final Undertow.Builder builder = Undertow.builder()
                .setHandler(root);
        if ("HTTPS".equalsIgnoreCase(configuration.protocol())) {
            builder.addHttpsListener(port, hostname, configuration.sslContext());
        } else {
            builder.addHttpListener(port, hostname);
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
        server = builder.build();
        server.start();
        // After the server starts we need to deploy
        deploy();
    }

    @Override
    public UndertowJaxrsServer start() {
        server = Undertow.builder()
                .addHttpListener(port, hostname)
                .setHandler(root)
                .build();
        server.start();
        return this;
    }

    @Override
    public void stop() {
        server.stop();

        if (deployment != null) {
            deployment.stop();
        }
    }

    public ResteasyDeployment getDeployment() {
        if (deployment == null) {
            deployment = new ResteasyDeploymentImpl();
        }
        return deployment;
    }

    @Override
    public UndertowJaxrsServer setDeployment(ResteasyDeployment deployment) {
        this.deployment = deployment;
        return this;
    }

    @Override
    public UndertowJaxrsServer setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public UndertowJaxrsServer setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Override
    public UndertowJaxrsServer setRootResourcePath(String rootResourcePath) {
        this.rootResourcePath = rootResourcePath;
        return this;
    }

    @Override
    public UndertowJaxrsServer setSecurityDomain(SecurityDomain sc) {
        // no-op; does not apply to undertow setup
        return this;
    }

    /*************************************************************************/

    public UndertowJaxrsServer deploy(Application application) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplication(application);
        return deploy(resteasyDeployment,
                serverHelper.checkAppPath(application.getClass().getAnnotation(ApplicationPath.class)),
                application.getClass().getClassLoader());
    }

    public UndertowJaxrsServer deploy(Application application, String contextPath) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplication(application);
        resteasyDeployment.start();
        return deploy(resteasyDeployment,
                serverHelper.checkContextPath(contextPath),
                application.getClass().getClassLoader());
    }

    public UndertowJaxrsServer deploy(Class<? extends Application> application) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplicationClass(application.getName());
        return deploy(resteasyDeployment,
                serverHelper.checkAppPath(application.getAnnotation(ApplicationPath.class)),
                resteasyDeployment.getClass().getClassLoader());
    }

    public UndertowJaxrsServer deploy(Class<? extends Application> application,
            String contextPath) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplicationClass(application.getName());
        return deploy(resteasyDeployment, serverHelper.checkContextPath(contextPath),
                resteasyDeployment.getClass().getClassLoader());
    }

    private UndertowJaxrsServer deploy(ResteasyDeployment resteasyDeployment,
            String contextPath, ClassLoader clazzLoader) {
        DeploymentInfo di = undertowDeployment(resteasyDeployment);
        populateDeploymentInfo(di, clazzLoader, contextPath);
        return deploy(di);
    }

    /**
     * Creates a web deployment for your ResteasyDeployent so you can set up things like security constraints
     * You'd call this method, add your servlet security constraints, then call deploy(DeploymentInfo)
     *
     * Note, only one ResteasyDeployment can be applied per DeploymentInfo
     * ResteasyServlet is mapped to mapping + "/*"
     *
     * Example:
     *
     * DeploymentInfo di = server.undertowDeployment(resteasyDeployment, "rest");
     * di.setDeploymentName("MyDeployment")
     * di.setContextRoot("root");
     * server.deploy(di);
     *
     * @param resteasyDeployment
     * @param mappingPrefix      resteasy.servlet.mapping.prefix
     * @return must be deployed by calling deploy(DeploymentInfo), also does not set context path or deployment name
     */
    public DeploymentInfo undertowDeployment(ResteasyDeployment resteasyDeployment, String mappingPrefix) {
        String mapping = serverHelper.checkContextPath(mappingPrefix);
        if (!mapping.endsWith("/")) {
            mapping += "/";
        }
        mapping = mapping + "*";

        ServletInfo resteasyServlet = servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addMapping(mapping);

        if (!mapping.equals("/*")) {
            String prefix = mapping.substring(0, mapping.length() - 2);
            resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);
        }

        return new DeploymentInfo()
                .addServletContextAttribute(ResteasyDeployment.class.getName(), resteasyDeployment)
                .addServlet(resteasyServlet);
    }

    public DeploymentInfo undertowDeployment(ResteasyDeployment resteasyDeployment) {
        String mapping;
        if (rootResourcePath != null) {
            mapping = serverHelper.checkContextPath(rootResourcePath);
        } else {
            mapping = serverHelper.checkAppDeployment(resteasyDeployment);
        }
        return undertowDeployment(resteasyDeployment, mapping);
    }

    public DeploymentInfo undertowDeployment(Class<? extends Application> application) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplicationClass(application.getName());
        DeploymentInfo di = undertowDeployment(resteasyDeployment,
                serverHelper.checkAppPath(application.getAnnotation(ApplicationPath.class)));
        di.setClassLoader(application.getClassLoader());
        return di;
    }

    /**
     * Maps a path prefix to a resource handler to allow serving resources other
     * than the JAX-RS endpoints.
     * For example, this can be used for serving static resources like web pages
     * or API documentation that might be deployed with the REST application server.
     *
     * @param path
     * @param handler
     */
    public void addResourcePrefixPath(String path, ResourceHandler handler) {
        root.addPrefixPath(path, handler);
    }

    public UndertowJaxrsServer deploy(ResteasyDeployment resteasyDeployment) {
        return deploy(resteasyDeployment, serverHelper.checkContextPath(rootResourcePath),
                resteasyDeployment.getClass().getClassLoader());
    }

    /**
     * Adds an arbitrary web deployment to underlying Undertow server.
     * This is for your own deployments
     *
     * @param builder
     * @return
     */
    public UndertowJaxrsServer deploy(DeploymentInfo builder) {
        manager = container.addDeployment(configureDefaults(builder, getDeployment()));
        manager.deploy();

        try {
            root.addPrefixPath(builder.getContextPath(), manager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public UndertowJaxrsServer start(Undertow.Builder builder) {
        server = builder.setHandler(root).build();
        server.start();
        return this;
    }

    public DeploymentManager getManager() {
        return manager;
    }

    public Map<String, String> getContextParams() {
        if (contextParams == null) {
            contextParams = new HashMap<>();
        }
        return contextParams;
    }

    public UndertowJaxrsServer setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
        return this;
    }

    public Map<String, String> getInitParams() {
        if (initParams == null) {
            initParams = new HashMap<>();
        }
        return initParams;
    }

    public UndertowJaxrsServer setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
        return this;
    }

    // OldStyle requires a mapping prefix to be "/" when the ApplicationPath is some other value
    public UndertowJaxrsServer deployOldStyle(Class<? extends Application> application) {
        return deployOldStyle(application, serverHelper.checkAppPath(application
                .getAnnotation(ApplicationPath.class)));
    }

    // OldStyle requires a mapping prefix to be "/" when the ApplicationPath is some other value
    public UndertowJaxrsServer deployOldStyle(Class<? extends Application> application,
            String ctxtPath) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeploymentImpl();
        resteasyDeployment.setApplicationClass(application.getName());
        String contextPath = serverHelper.checkContextPath(ctxtPath);
        DeploymentInfo di = undertowDeployment(resteasyDeployment, "/");
        populateDeploymentInfo(di, resteasyDeployment.getClass().getClassLoader(), contextPath);
        return deploy(di);
    }

    private void populateDeploymentInfo(DeploymentInfo di, ClassLoader clazzLoader,
            String contextPath) {
        di.setClassLoader(clazzLoader);
        di.setContextPath(contextPath);
        di.setDeploymentName("Resteasy" + contextPath);

        if (contextParams != null) {
            for (Entry<String, String> e : contextParams.entrySet()) {
                di.addInitParameter(e.getKey(), e.getValue());
            }
        }
        if (initParams != null) {
            ServletInfo servletInfo = di.getServlets().get("ResteasyServlet");
            for (Entry<String, String> e : initParams.entrySet()) {
                servletInfo.addInitParam(e.getKey(), e.getValue());
            }
        }

    }

    private DeploymentInfo configureDefaults(final DeploymentInfo deploymentInfo, final ResteasyDeployment deployment) {
        // Check for a default multipart config. If not found and the application class is set, check there.
        if (deploymentInfo.getDefaultMultipartConfig() == null) {
            final Application application = deployment.getApplication();
            if (application != null) {
                final MultipartConfig multipartConfig = application.getClass().getAnnotation(MultipartConfig.class);
                if (multipartConfig != null) {
                    deploymentInfo.setDefaultMultipartConfig(new MultipartConfigElement(multipartConfig));
                }
            }
        }
        return deploymentInfo;
    }
}
