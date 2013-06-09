package org.jboss.resteasy.jsapi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;


/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class JSAPIServlet extends HttpServlet
{

   private static final long serialVersionUID = -1985015444704126795L;

   private final static Logger logger = Logger.getLogger(JSAPIServlet.class);
   private Map<String, ServiceRegistry> services;

   private JSAPIWriter apiWriter = new JSAPIWriter();

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      if (logger.isDebugEnabled())
         logger.info("Loading JSAPI Servlet");

      scanResources();

      if (logger.isDebugEnabled())
         logger.debug("JSAPIServlet loaded");

      // make it possible to get to us for rescanning
      ServletContext servletContext = config.getServletContext();
      servletContext.setAttribute(getClass().getName(), this);
   }

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException
   {
      String pathInfo = req.getPathInfo();
      String uri = req.getRequestURL().toString();
      uri = uri.substring(0, uri.length() - req.getServletPath().length());
      if (logger.isDebugEnabled())
      {
         logger.debug("Serving " + pathInfo);
         logger.debug("Query " + req.getQueryString());
      }
      if (this.services == null) scanResources();
      if (this.services == null)
      {
         resp.sendError(503, "There are no Resteasy deployments initialized yet to scan from.  Either set the load-on-startup on each Resteasy servlet, or, if in an EE environment like JBoss or Wildfly, you'll have to do an invocation on each of your REST services to get the servlet loaded.");
      }
      this.apiWriter.writeJavaScript(uri, req, resp, services);
   }

   public void scanResources()
   {

      ServletConfig config = getServletConfig();
      ServletContext servletContext = config.getServletContext();
      Map<String, ResteasyDeployment> deployments = (Map<String, ResteasyDeployment>) servletContext.getAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS);

      if (deployments == null) return;
      synchronized (this)
      {
         services = new HashMap<String, ServiceRegistry>();
         for (Map.Entry<String, ResteasyDeployment> entry : deployments.entrySet())
         {
            ResourceMethodRegistry registry = (ResourceMethodRegistry) entry.getValue().getRegistry();
            ResteasyProviderFactory providerFactory =
                    (ResteasyProviderFactory) entry.getValue().getProviderFactory();
            ServiceRegistry service = new ServiceRegistry(null, registry, providerFactory, null);
            services.put(entry.getKey(), service);
         }
      }
   }
}
