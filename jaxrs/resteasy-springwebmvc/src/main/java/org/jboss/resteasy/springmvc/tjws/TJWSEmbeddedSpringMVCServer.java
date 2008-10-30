package org.jboss.resteasy.springmvc.tjws;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.resteasy.core.DefaultInterceptors;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSServletServer;
import org.springframework.context.ApplicationContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSEmbeddedSpringMVCServer extends TJWSServletServer {
	protected TJWSSpringMVCDispatcher servlet = new TJWSSpringMVCDispatcher();
	Class[] defaultResourceMethodInterceptors = DefaultInterceptors.defaultInterceptors;

	protected String rootResourcePath = "";

	public void setRootResourcePath(String rootResourcePath) {
		this.rootResourcePath = rootResourcePath;
	}

	public TJWSEmbeddedSpringMVCServer() {
	}

	public void setDefaultResourceMethodInterceptors(Class[] interceptorClasses) {
		defaultResourceMethodInterceptors = interceptorClasses;
	}

	@Override
	public void start() {
		servlet.setContextPath(rootResourcePath);
		addServlet(rootResourcePath, servlet);
		super.start();
	}

	private ServletConfig getServletConfig() {
		return new ServletConfig() {

			public String getInitParameter(String paramName) {
				return server.getInitParameter(paramName);
			}

			public Enumeration getInitParameterNames() {
				return server.getInitParameterNames();
			}

			public ServletContext getServletContext() {
				return server;
			}

			public String getServletName() {
				return "tjwsServlet";
			}
		};
	}

	public void setSecurityDomain(SecurityDomain sc) {
		servlet.setSecurityDomain(sc);
	}

	public void setSpringConfigLocation(String configFile) {
		servlet.setContextConfigLocation(configFile);
	}

	public ApplicationContext getApplicationContext() {
		return servlet.getWebApplicationContext();
	}

}
