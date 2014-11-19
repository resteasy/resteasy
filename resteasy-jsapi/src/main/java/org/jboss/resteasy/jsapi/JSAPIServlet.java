package org.jboss.resteasy.jsapi;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.resteasy_jsapi.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jsapi.i18n.Messages;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 *     <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class JSAPIServlet extends HttpServlet
{

	private static final long serialVersionUID = -1985015444704126795L;

	private ServiceRegistry service;

	private JSAPIWriter apiWriter;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		LogMessages.LOGGER.debug(Messages.MESSAGES.loadingJSAPIServlet());

		scanResources();
		
		LogMessages.LOGGER.debug(Messages.MESSAGES.jsapiServletLoaded());

		// make it possible to get to us for rescanning
		ServletContext servletContext = config .getServletContext();
		servletContext.setAttribute(getClass().getName(), this);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		String pathInfo = req.getPathInfo();
		String uri = req.getRequestURL().toString();
		uri = uri.substring(0, uri.length() - req.getServletPath().length());
		LogMessages.LOGGER.debug(Messages.MESSAGES.serving(pathInfo));
		LogMessages.LOGGER.debug(Messages.MESSAGES.query(req.getQueryString()));	
        this.apiWriter.writeJavaScript(uri, req, resp, service);
	}

	public void scanResources(){

		ServletConfig config = getServletConfig();
		ServletContext servletContext = config .getServletContext();
		ResourceMethodRegistry registry = (ResourceMethodRegistry) servletContext
		.getAttribute(Registry.class.getName());
		ResteasyProviderFactory providerFactory = 
			(ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());

		String restPath = servletContext
		.getInitParameter("resteasy.servlet.mapping.prefix");

		service = new ServiceRegistry(null, registry, providerFactory, null);
		apiWriter = new JSAPIWriter(restPath);
	}
}
