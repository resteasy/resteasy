package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.io.IOException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

@NameBoundProxiesAnnotation
public class NameBoundCDIProxiesInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

   private static String in = "";

   /** The application context, used for retrieving the {@link ApplicationPath} value. */
   @Context Application application;

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
      Object entity = isJavassist(application) ? in + responseContext.getEntity() + "-out" : responseContext.getEntity();
      responseContext.setEntity(entity);
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      in = "in-";
   }

   /**
    * Verify that application is a javassist proxy, which implies that CDI is activated.
    */
   private boolean isJavassist(Application application) {
      return application.getClass().getName().contains("$$_jvst");
   }
}
