package org.jboss.resteasy.plugins.server.undertow.spring;


import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.springframework.web.servlet.DispatcherServlet;

import static io.undertow.servlet.Servlets.servlet;

public class UndertowJaxrsSpringServer extends UndertowJaxrsServer {

   public static final String SERVLET_NAME = "ResteasyServlet";

   public DeploymentInfo undertowDeployment(String contextConfigLocation, String mapping) {
      if (mapping == null) mapping = "/";
      if (!mapping.startsWith("/")) mapping = "/" + mapping;
      if (!mapping.endsWith("/")) mapping += "/";
      mapping = mapping + "*";
      String prefix = null;

      if (!mapping.equals("/*"))
         prefix = mapping.substring(0, mapping.length() - 2);

      ServletInfo servlet =
            servlet(SERVLET_NAME, DispatcherServlet.class)
                  .setLoadOnStartup(1)
                  .addInitParam("contextConfigLocation", contextConfigLocation)
                  .addMapping(mapping);

      DeploymentInfo deployment = new DeploymentInfo()
            .addServlet(servlet);
      if (prefix != null)
         deployment.setContextPath(prefix);
      return deployment;

   }

}
