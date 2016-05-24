package org.jboss.resteasy.test.application;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class FooExceptionMapper implements ExceptionMapper<FooException>
{
   private MyApplication application;
   private ServletConfig servletConfig;
   private ServletContext context;

   public FooExceptionMapper(@Context Application application, @Context ServletConfig servletConfig, @Context ServletContext context)
   {
      this.application = (MyApplication)application;
      this.servletConfig = servletConfig;
      System.out.println("greeting: " + this.application.getHello());
      System.out.println("servlet greeting: " + servletConfig.getInitParameter("servlet.greeting"));
      System.out.println("contet greeting: " + context.getInitParameter("context.greeting"));
   }

   public Response toResponse(FooException exception)
   {
      return Response.status(412).build();
   }
}
