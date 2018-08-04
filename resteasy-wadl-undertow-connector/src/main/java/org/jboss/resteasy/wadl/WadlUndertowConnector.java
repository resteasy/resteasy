package org.jboss.resteasy.wadl;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static io.undertow.servlet.Servlets.servlet;

/**
 * Created by weli on 7/26/16.
 */
public class WadlUndertowConnector {
   public UndertowJaxrsServer deployToServer(UndertowJaxrsServer server, Class<? extends Application> application) {
      ApplicationPath appPath = application.getAnnotation(ApplicationPath.class);
      String path = "/";
      if (appPath != null) path = appPath.value();

      return deployToServer(server, application, path);
   }

   public UndertowJaxrsServer deployToServer(UndertowJaxrsServer server, Class<? extends Application> application, String contextPath) {
      ResteasyDeployment deployment = new ResteasyDeploymentImpl();
      deployment.setApplicationClass(application.getName());

      DeploymentInfo di = server.undertowDeployment(deployment);

      di.setClassLoader(application.getClassLoader());
      di.setContextPath(contextPath);
      di.setDeploymentName("Resteasy" + contextPath);
      return server.deploy(di);
   }
}
