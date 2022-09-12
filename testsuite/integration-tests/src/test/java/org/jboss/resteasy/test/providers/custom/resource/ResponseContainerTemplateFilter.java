package org.jboss.resteasy.test.providers.custom.resource;


import org.jboss.logging.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Method;

public abstract class ResponseContainerTemplateFilter implements ContainerResponseFilter {

   public static final String OPERATION = "OPERATION";
   private static Logger logger = Logger.getLogger(ResponseContainerTemplateFilter.class);

   protected ContainerRequestContext requestContext;
   protected ContainerResponseContext responseContext;

   @Override
   public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
      this.requestContext = requestContext;
      this.responseContext = responseContext;
      String operation = getHeaderString();
      Method[] methods = getClass().getMethods();
      for (Method method : methods) {
         if (operation != null) {
            if (operation.equalsIgnoreCase(method.getName())) {
               try {
                  method.invoke(this);
                  return;
               } catch (Exception e) {
                  logger.error("The requested resource is not available", e);
                  responseContext.setStatus(Response.Status.SERVICE_UNAVAILABLE
                        .getStatusCode());
                  setEntity(e.getMessage());
                  return;
               }
            }
         }
      }
      operationMethodNotFound(operation);
   }

   protected void operationMethodNotFound(String operation) {
      responseContext.setStatus(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
      setEntity("Operation " + operation + " not implemented");
   }

   // ///////////////////////////////////////////////////////////////////
   protected boolean assertTrue(boolean conditionTrue, Object... msg) {
      if (conditionTrue) {
         return false;
      }
      StringBuilder sb = new StringBuilder();
      if (msg != null) {
         for (Object str : msg) {
            sb.append(str).append(" ");
         }
      }
      setEntity(sb.toString());
      responseContext.setStatus(Response.Status.NOT_ACCEPTABLE.getStatusCode());
      return true;
   }

   protected String getHeaderString() {
      MultivaluedMap<String, Object> headers = responseContext.getHeaders();
      return (String) headers.getFirst(OPERATION);
   }

   protected void setEntity(String entity) {
      responseContext.setEntity(entity, null, MediaType.TEXT_PLAIN_TYPE);
   }

}
