package org.jboss.resteasy.grpc.servlet;

import java.util.Enumeration;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;

public class ServletConfigWrapper implements ServletConfig {

   public static final String GRPC_JAXRS = "grpcJaxrs";
   ServletConfig delegate;
   String readerWriter;

   public ServletConfigWrapper(ServletConfig delegate, String readerWriter) {
      this.delegate = delegate;
      this.readerWriter = readerWriter;
   }

   @Override
   public String getServletName() {
      return delegate.getServletName();
   }

   @Override
   public ServletContext getServletContext() {
      return delegate.getServletContext();
   }

   @Override
   public String getInitParameter(String name) {
      if (GRPC_JAXRS.equals(name)) {
         return "true";
      }
      if (ResteasyContextParameters.RESTEASY_PROVIDERS.equals(name)) {
         return readerWriter;
      }
      return delegate.getInitParameter(name);
   }

   @Override
   public Enumeration<String> getInitParameterNames() {
      return delegate.getInitParameterNames();
   }
}
