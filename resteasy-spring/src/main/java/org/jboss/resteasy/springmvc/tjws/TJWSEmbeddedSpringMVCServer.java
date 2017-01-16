package org.jboss.resteasy.springmvc.tjws;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSServletServer;
import org.springframework.context.ApplicationContext;

/**
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
* 
*/
@Deprecated
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
		this(applicationContext, port, "");
	}
    public TJWSEmbeddedSpringMVCServer(String applicationContext, int port, String rootResourcePath) {
       setSpringConfigLocation(applicationContext);
       setPort(port);
       setRootResourcePath(rootResourcePath);
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
