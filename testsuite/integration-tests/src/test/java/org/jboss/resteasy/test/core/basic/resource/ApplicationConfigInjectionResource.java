package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

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
