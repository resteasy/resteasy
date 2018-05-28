package org.jboss.resteasy.jsapi;

import java.io.IOException;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.jsapi.i18n.LogMessages;
import org.jboss.resteasy.jsapi.i18n.Messages;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;


/**
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class JSAPIServlet extends HttpServlet
{

   //corresponding to RFC 4329 this is the right MEDIA_TYPE
   private static final String JS_MEDIA_TYPE = "application/javascript";

   private static final long serialVersionUID = -1985015444704126795L;

   private Map<String, ServiceRegistry> services;

   private JSAPIWriter apiWriter = new JSAPIWriter();

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      if (LogMessages.LOGGER.isDebugEnabled())
         LogMessages.LOGGER.info(Messages.MESSAGES.loadingJSAPIServlet());

      try {
         scanResources();
      } catch (Exception e) {
         throw new ServletException(e);
      }

      if (LogMessages.LOGGER.isDebugEnabled())
         LogMessages.LOGGER.debug(Messages.MESSAGES.jsapiServletLoaded());

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
      if (LogMessages.LOGGER.isDebugEnabled())
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.serving(pathInfo));
         LogMessages.LOGGER.debug(Messages.MESSAGES.query(req.getQueryString()));
      }
      if (this.services == null) try {
         scanResources();
      } catch (Exception e) {
         resp.sendError(503, Messages.MESSAGES.thereAreNoResteasyDeployments()); // FIXME should return internal error
      }

      if (this.services == null)
      {
         resp.sendError(503, Messages.MESSAGES.thereAreNoResteasyDeployments());
      }
      resp.setContentType(JS_MEDIA_TYPE);
      this.apiWriter.writeJavaScript(uri, req, resp, services);

   }

   public void scanResources() throws Exception {

      ServletConfig config = getServletConfig();
      ServletContext servletContext = config.getServletContext();
      @SuppressWarnings(value = "unchecked")
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
