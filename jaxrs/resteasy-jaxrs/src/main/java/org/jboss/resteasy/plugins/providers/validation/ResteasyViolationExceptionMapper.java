package org.jboss.resteasy.plugins.providers.validation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 31, 2012
 */
@Provider
public class ResteasyViolationExceptionMapper implements ExceptionMapper<ResteasyViolationExceptionExtension>
{
   public Response toResponse(ResteasyViolationExceptionExtension exception)
   {
//      exception.convertToStrings();
//      exception.setViolationsContainer(null);
      if (exception.getReturnValueViolations().size() == 0)
      {
//        return Response.status(Status.BAD_REQUEST).type("application/x-java-serialized-object").entity(exception).build();
         return Response.status(Status.BAD_REQUEST).entity(exception.getStrings()).build();
      }
      else
      {
//         return Response.status(Status.INTERNAL_SERVER_ERROR).type("application/x-java-serialized-object").entity(exception).build();
         return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getStrings()).build();
         
      }
   }
}
