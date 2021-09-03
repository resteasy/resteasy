package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
@ApplicationPath("/")
public class ApplicationInjection extends Application {
   public static volatile List<ApplicationInjection> instances = new ArrayList<>();

   @Context
   public Application app;

   public ApplicationInjection() {
      instances.add(this);
   }
}
