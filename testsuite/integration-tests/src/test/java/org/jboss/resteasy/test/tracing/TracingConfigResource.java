package org.jboss.resteasy.test.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

@Path("/")
public class TracingConfigResource extends Application {

   @GET
   @Path("/type")
   public String type(@Context ResteasyDeployment deployment) {
      return RESTEasyTracingLogger.getTracingConfig(deployment.getProviderFactory());
   }

   @GET
   @Path("/level")
   public String level(@Context ResteasyDeployment deployment) {
      return RESTEasyTracingLogger.getTracingThreshold(deployment.getProviderFactory());
   }

   @GET
   @Path("/logger")
   public String logger(@Context HttpRequest request) {
      RESTEasyTracingLogger logger = (RESTEasyTracingLogger) request.getAttribute(RESTEasyTracing.PROPERTY_NAME);
      if (logger == null) {
         return "";
      } else {
         return RESTEasyTracingLogger.class.getName();
      }
   }

}
