package org.jboss.resteasy.test.async;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyApp extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> classes = new HashSet<Class<?>>();

   @Provider
   public static class PrintingErrorHandler implements ExceptionMapper<Throwable>
   {

      @Override
      public Response toResponse(Throwable throwable) {
         throwable.printStackTrace();

         Writer result = new StringWriter();
         PrintWriter printWriter = new PrintWriter(result);
         throwable.printStackTrace(printWriter);
         return Response.status(Response.Status.NOT_ACCEPTABLE).entity(result.toString())
                 .build();
      }

   }
   public MyApp()
   {
      classes.add(Resource.class);
      classes.add(ServiceUnavailableExceptionMapper.class);
      classes.add(PrintingErrorHandler.class);
      singletons.add(new MyResource());
      singletons.add(new JaxrsResource());
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

}
