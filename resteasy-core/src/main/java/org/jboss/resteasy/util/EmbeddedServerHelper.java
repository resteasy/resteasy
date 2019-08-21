package org.jboss.resteasy.util;

import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.ws.rs.ApplicationPath;

public class EmbeddedServerHelper {

   public void checkDeployment(final ResteasyDeployment deployment) {
      if (deployment == null) {
         throw new IllegalArgumentException("A ResteasyDeployment object required");
      } else if (deployment.getRegistry() == null) {
         deployment.start();
      }
   }

   public String checkAppDeployment(final ResteasyDeployment deployment) {

      ResteasyDeployment appDeployment = deployment;

      ApplicationPath appPath = null;
      if (deployment.getApplicationClass() != null)
      {
         try
         {
            Class clazz = Class.forName(deployment.getApplicationClass());
            appPath = (ApplicationPath) clazz.getAnnotation(ApplicationPath.class);

         } catch (ClassNotFoundException e)
         {
            // todo how to handle
         }
      } else if (deployment.getApplication() != null)
      {
         appPath = deployment.getApplication().getClass().getAnnotation(ApplicationPath.class);
      }

      String aPath = null;
      if (appPath != null){
         aPath = appPath.value();
      }
      return aPath;
   }


   public String checkAppPath(final ApplicationPath appPath) {
      if (appPath != null) {
         return appPath.value();
      }
      return "/";
   }


   public String checkContextPath(String contextPath) {
      if (contextPath == null) {
         return "/";
      } else if (!contextPath.startsWith("/")) {
         return "/" + contextPath;
      }
      return contextPath;
   }

}
