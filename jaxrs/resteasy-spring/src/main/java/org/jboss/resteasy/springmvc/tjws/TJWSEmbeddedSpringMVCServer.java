package org.jboss.resteasy.springmvc.tjws;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSServletServer;
import org.springframework.context.ApplicationContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSEmbeddedSpringMVCServer extends TJWSServletServer {
	protected TJWSSpringMVCDispatcher servlet = new TJWSSpringMVCDispatcher();
//	Class<>[] defaultResourceMethodInterceptors = DefaultInterceptors.defaultInterceptors;

	protected String rootResourcePath = "";

	public void setRootResourcePath(String rootResourcePath) {
		this.rootResourcePath = rootResourcePath;
	}

	public TJWSEmbeddedSpringMVCServer() {
	}

	public TJWSEmbeddedSpringMVCServer(String applicationContext, int port) {
		setSpringConfigLocation(applicationContext);
		setPort(port);
	}

//	public void setDefaultResourceMethodInterceptors(Class[] interceptorClasses) {
//		defaultResourceMethodInterceptors = interceptorClasses;
//	}

	@Override
	public void start() {
		servlet.setContextPath(rootResourcePath);
		addServlet(rootResourcePath, servlet);
		super.start();
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
