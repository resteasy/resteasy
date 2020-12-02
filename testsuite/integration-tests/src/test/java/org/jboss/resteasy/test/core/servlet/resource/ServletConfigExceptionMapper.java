package org.jboss.resteasy.test.core.servlet.resource;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.Assert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ServletConfigExceptionMapper implements ExceptionMapper<ServletConfigException> {
   private ServletConfigApplication application;
   private ServletConfig servletConfig;
   private ServletContext context;

   public ServletConfigExceptionMapper(@Context final Application application,
                              @Context final ServletConfig servletConfig, @Context final ServletContext context) {
      this.application = (ServletConfigApplication) application;
      this.servletConfig = servletConfig;
      this.context = context;
      Assert.assertEquals("hello", this.application.getHello());
      Assert.assertEquals("servlet hello", this.servletConfig.getInitParameter("servlet.greeting"));
      Assert.assertEquals("context hello", this.context.getInitParameter("context.greeting"));
   }

   public Response toResponse(ServletConfigException exception) {
      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).build();
   }
}
