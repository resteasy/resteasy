package org.jboss.resteasy.plugins.providers.validation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.SerializableProvider;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 31, 2012
 */
@Provider
public class ResteasyViolationExceptionMapper implements ExceptionMapper<ResteasyViolationException>
{
   public Response toResponse(ResteasyViolationException exception)
   {
//      exception.convertToStrings();
//      exception.setViolationsContainer(null);
      if (exception.getReturnValueViolations().size() == 0)
      {
//        return Response.status(Status.BAD_REQUEST).type("application/x-java-serialized-object").entity(exception).build();
//         return Response.status(Status.BAD_REQUEST).entity(exception.getStrings()).build();
         return buildResponse(exception, SerializableProvider.APPLICATION_SERIALIZABLE, Status.BAD_REQUEST); 
      }
      else
      {
//         return Response.status(Status.INTERNAL_SERVER_ERROR).type("application/x-java-serialized-object").entity(exception).build();
//         return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getStrings()).build();
         return buildResponse(exception, SerializableProvider.APPLICATION_SERIALIZABLE, Status.INTERNAL_SERVER_ERROR);
      }
   }
   
   protected Response buildResponse(Object entity, String mediaType, Status status)
   {
      ResponseBuilder builder =  Response.status(status).entity(entity);
      builder.type(SerializableProvider.APPLICATION_SERIALIZABLE);
      builder.header(ValidationSupport.VALIDATION_HEADER, "true");
      return builder.build();
   }
}
