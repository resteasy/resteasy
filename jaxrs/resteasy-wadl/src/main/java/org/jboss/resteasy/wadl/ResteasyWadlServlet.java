package org.jboss.resteasy.wadl;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlServlet extends HttpServlet {

    private final static Logger logger = Logger.getLogger(ResteasyWadlServlet.class);
    private Map<String, ResteasyWadlServiceRegistry> services;

    private ResteasyWadlServletWriter apiWriter = new ResteasyWadlServletWriter();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (logger.isDebugEnabled())
            logger.info("Loading ResteasyWadlServlet");

        scanResources();

        if (logger.isDebugEnabled())
            logger.debug("ResteasyWadlServlet loaded");

        // make it possible to get to us for rescanning
        ServletContext servletContext = config.getServletContext();
        servletContext.setAttribute(getClass().getName(), this);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURL().toString();
        uri = uri.substring(0, uri.length() - req.getServletPath().length());
        if (logger.isDebugEnabled()) {
            logger.debug("Serving " + pathInfo);
            logger.debug("Query " + req.getQueryString());
        }
        if (this.services == null) scanResources();
        if (this.services == null) {
            resp.sendError(503, "There are no Resteasy deployments initialized yet to scan from. Either set the load-on-startup on each Resteasy servlet, or, if in an EE environment like JBoss or Wildfly, you'll have to do an invocation on each of your REST services to get the servlet loaded.");
        }
        resp.setContentType(MediaType.APPLICATION_XML);
        this.apiWriter.writeWadl(uri, req, resp, services);
    }

    public void scanResources() {

        ServletConfig config = getServletConfig();
        ServletContext servletContext = config.getServletContext();
        Map<String, ResteasyDeployment> deployments = (Map<String, ResteasyDeployment>) servletContext.getAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS);
        if (deployments == null) return;
        synchronized (this) {
            services = new HashMap<String, ResteasyWadlServiceRegistry>();
            for (Map.Entry<String, ResteasyDeployment> entry : deployments.entrySet()) {
                services.put(entry.getKey(), ResteasyWadlGenerator.generateServiceRegistry(entry.getValue()));
            }
        }
    }

}
