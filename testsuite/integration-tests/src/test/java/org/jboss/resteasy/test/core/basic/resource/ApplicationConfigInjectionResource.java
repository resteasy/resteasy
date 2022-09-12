package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;

@Path("/injection")
@Produces("text/plain")
public class ApplicationConfigInjectionResource {
   private ApplicationConfig application;

   @Path("/field")
   @GET
   public boolean fieldInjection() {
      return getApplication().isFieldInjected();
   }

   @Path("/setter")
   @GET
   public boolean setterInjection() {
      return getApplication().isSetterInjected();
   }

   @Path("/constructor")
   @GET
   public boolean constructorInjection() {
      return getApplication().isConstructorInjected();
   }

   private ApplicationConfig getApplication() {
      return application;
   }

   @Context
   public void setApplication(Application app) {
      this.application = (ApplicationConfig) app;
   }
}
