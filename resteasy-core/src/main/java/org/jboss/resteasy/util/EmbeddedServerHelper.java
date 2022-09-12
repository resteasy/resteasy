package org.jboss.resteasy.util;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.spi.ResteasyDeployment;

import jakarta.ws.rs.ApplicationPath;

public class EmbeddedServerHelper {

   public void checkDeployment(final ResteasyDeployment deployment) {
      EmbeddedServers.validateDeployment(deployment);
   }

   public String checkAppDeployment(final ResteasyDeployment deployment) {
      return EmbeddedServers.resolveContext(deployment);
   }


   public String checkAppPath(final ApplicationPath appPath) {
      if (appPath != null) {
         return appPath.value();
      }
      return "/";
   }


   public String checkContextPath(String contextPath) {
      return EmbeddedServers.checkContextPath(contextPath);
   }

}
