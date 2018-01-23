package org.jboss.resteasy.test.providers.injection.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class ApplicationInjectionWriterInterceptor implements WriterInterceptor {
   
   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
      context.setEntity((String) context.getEntity() + "|" + context.getProperty("readerInterceptorApplication") + "|" + getClass() + ":" + application.getName());
      context.proceed();
   }
}
