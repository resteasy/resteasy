package org.jboss.resteasy.plugins.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public class ServletContainerDispatcher {
    protected Dispatcher dispatcher;
    protected ResteasyProviderFactory providerFactory;
    private String servletMappingPrefix = "";
    protected ResteasyDeployment deployment = null;
    protected HttpRequestFactory requestFactory;
    protected HttpResponseFactory responseFactory;

    protected ServletConfig servletConfig;

    public ServletContainerDispatcher(final ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        ResteasyContext.pushContext(ServletConfig.class, servletConfig);
    }

    public ServletContainerDispatcher() {
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    @SuppressWarnings(value = "unchecked")
    public void init(ServletContext servletContext, ConfigurationBootstrap bootstrap, HttpRequestFactory requestFactory,
            HttpResponseFactory responseFactory) throws ServletException {
        this.requestFactory = requestFactory;
        this.responseFactory = responseFactory;
        ResteasyDeployment ctxDeployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
        ResteasyProviderFactory globalFactory = (ResteasyProviderFactory) servletContext
                .getAttribute(ResteasyProviderFactory.class.getName());
        if (globalFactory == null && ctxDeployment != null) {
            globalFactory = ctxDeployment.getProviderFactory();
        }
        Dispatcher globalDispatcher = (Dispatcher) servletContext.getAttribute(Dispatcher.class.getName());
        if (globalDispatcher == null && ctxDeployment != null) {
            globalDispatcher = ctxDeployment.getDispatcher();
        }

        String application = bootstrap.getInitParameter("jakarta.ws.rs.Application");
        String useGlobalStr = bootstrap.getInitParameter("resteasy.servlet.context.deployment");
        boolean useGlobal = globalFactory != null;
        if (useGlobalStr != null)
            useGlobal = Boolean.parseBoolean(useGlobalStr);
        // use global is backward compatible with 2.3.x and earlier and will store and/or use the dispatcher and provider factory
        // in the servlet context
        if (useGlobal) {
            providerFactory = globalFactory;
            dispatcher = globalDispatcher;
            if ((providerFactory != null && dispatcher == null) || (providerFactory == null && dispatcher != null)) {
                throw new ServletException(Messages.MESSAGES.unknownStateListener());
            }
            // We haven't been initialized by an external entity so bootstrap ourselves
            if (providerFactory == null) {
                deployment = bootstrap.createDeployment();
                deployment.start();

                servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
                servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
                servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());

                dispatcher = deployment.getDispatcher();
                providerFactory = deployment.getProviderFactory();

            } else {
                // ResteasyBootstrap inited us.  Check to see if the servlet defines an Application class
                if (application != null) {
                    try {
                        Map contextDataMap = ResteasyContext.getContextDataMap();
                        contextDataMap.putAll(dispatcher.getDefaultContextObjects());
                        Application app = ResteasyDeploymentImpl.createApplication(application.trim(), dispatcher,
                                providerFactory);
                        // push context data so we can inject it
                        processApplication(app);
                        servletMappingPrefix = bootstrap
                                .getParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX);
                        if (servletMappingPrefix == null)
                            servletMappingPrefix = "";
                        servletMappingPrefix = servletMappingPrefix.trim();
                    } finally {
                        ResteasyContext.removeContextDataLevel();
                    }
                } else {
                    servletMappingPrefix = bootstrap.getParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX);
                    if (servletMappingPrefix == null)
                        servletMappingPrefix = "";
                    servletMappingPrefix = servletMappingPrefix.trim();
                }
            }
        } else {
            servletMappingPrefix = bootstrap.getParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX);
            if (servletMappingPrefix == null)
                servletMappingPrefix = "";
            servletMappingPrefix = servletMappingPrefix.trim();

            deployment = bootstrap.createDeployment();
            deployment.start();
            dispatcher = deployment.getDispatcher();
            providerFactory = deployment.getProviderFactory();
        }
    }

    public void destroy() {
        if (deployment != null) {
            deployment.stop();
        }
    }

    protected void processApplication(Application config) {
        LogMessages.LOGGER.deployingApplication(Application.class.getName(), config.getClass());
        ArrayList<Class> actualResourceClasses = new ArrayList<Class>();
        ArrayList<Class> actualProviderClasses = new ArrayList<Class>();
        ArrayList<Object> resources = new ArrayList<>();
        ArrayList<Object> providers = new ArrayList<>();
        if (config.getClasses() != null) {
            for (Class clazz : config.getClasses()) {
                if (GetRestful.isRootResource(clazz)) {
                    LogMessages.LOGGER.addingClassResource(clazz.getName(), config.getClass());
                    actualResourceClasses.add(clazz);
                } else {
                    LogMessages.LOGGER.addingProviderClass(clazz.getName(), config.getClass());
                    actualProviderClasses.add(clazz);
                }
            }
        }
        if (config.getSingletons() != null) {
            for (Object obj : config.getSingletons()) {
                if (GetRestful.isRootResource(obj.getClass())) {
                    LogMessages.LOGGER.addingSingletonResource(obj.getClass().getName(), config.getClass());
                    resources.add(obj);
                } else {
                    LogMessages.LOGGER.addingSingletonProvider(obj.getClass().getName(), config.getClass());
                    providers.add(obj);
                }
            }
        }
        for (Class clazz : actualProviderClasses)
            providerFactory.registerProvider(clazz);
        for (Object obj : providers)
            providerFactory.registerProviderInstance(obj);
        for (Class clazz : actualResourceClasses)
            dispatcher.getRegistry().addPerRequestResource(clazz);
        for (Object obj : resources)
            dispatcher.getRegistry().addSingletonResource(obj);
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response, boolean handleNotFound)
            throws IOException, NotFoundException {
        try {
            //logger.info(httpMethod + " " + request.getRequestURL().toString());
            //logger.info("***PATH: " + request.getRequestURL());
            // classloader/deployment aware RestasyProviderFactory.  Used to have request specific
            // ResteasyProviderFactory.getInstance()
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.push(providerFactory);
            }
            ResteasyHttpHeaders headers = null;
            ResteasyUriInfo uriInfo = null;
            try {
                headers = ServletUtil.extractHttpHeaders(request);
                uriInfo = ServletUtil.extractUriInfo(request, servletMappingPrefix);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                // made it warn so that people can filter this.
                LogMessages.LOGGER.failedToParseRequest(e);
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                // made it warn so that people can filter this.
                LogMessages.LOGGER.failedToParseRequest(e);
                return;
            }

            try (HttpResponse theResponse = responseFactory.createResteasyHttpResponse(response, request)) {
                HttpRequest in = requestFactory.createResteasyHttpRequest(httpMethod, request, headers, uriInfo, theResponse,
                        response);

                ResteasyContext.pushContext(HttpServletRequest.class, request);
                ResteasyContext.pushContext(HttpServletResponse.class, response);

                ResteasyContext.pushContext(SecurityContext.class, new ServletSecurityContext(request));
                ResteasyContext.pushContext(ServletConfig.class, servletConfig);

                if (handleNotFound) {
                    dispatcher.invoke(in, theResponse);
                } else {
                    ((SynchronousDispatcher) dispatcher).invokePropagateNotFound(in, theResponse);
                }
            } finally {
                ResteasyContext.clearContextData();
            }
        } finally {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.pop();
            }

        }
    }
}
