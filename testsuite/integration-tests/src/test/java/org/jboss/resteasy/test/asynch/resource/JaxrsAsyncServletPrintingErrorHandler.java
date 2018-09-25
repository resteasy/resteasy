package org.jboss.resteasy.test.asynch.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Provider
public class JaxrsAsyncServletPrintingErrorHandler implements ExceptionMapper<Throwable> {
   private Logger logger = Logger.getLogger(JaxrsAsyncServletApp.class);

   @Override
   public Response toResponse(Throwable throwable) {
      StringWriter errors = new StringWriter();
      throwable.printStackTrace(new PrintWriter(errors));
      logger.error(errors.toString());

      Writer result = new StringWriter();
      PrintWriter printWriter = new PrintWriter(result);
      throwable.printStackTrace(printWriter);
      return Response.status(Response.Status.NOT_ACCEPTABLE).entity(result.toString())
            .build();
   }

}
