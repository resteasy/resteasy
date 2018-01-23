package org.jboss.resteasy.test.providers.injection.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class ApplicationInjectionReaderInterceptor implements ReaderInterceptor {
   
   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      context.setProperty("readerInterceptorApplication", getClass() + ":" + application.getName());
      return context.proceed();
   }
}
