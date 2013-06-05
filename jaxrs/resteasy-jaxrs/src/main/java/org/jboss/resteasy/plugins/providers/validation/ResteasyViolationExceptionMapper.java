package org.jboss.resteasy.plugins.providers.validation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.SerializableProvider;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;
import org.jboss.resteasy.spi.validation.Validation;


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
      if (exception.getReturnValueViolations().size() == 0)
      {
         return buildResponse(exception, SerializableProvider.APPLICATION_SERIALIZABLE, Status.BAD_REQUEST); 
      }
      else
      {
         return buildResponse(exception, SerializableProvider.APPLICATION_SERIALIZABLE, Status.INTERNAL_SERVER_ERROR);
      }
   }
   
   protected Response buildResponse(Object entity, String mediaType, Status status)
   {
      ResponseBuilder builder =  Response.status(status).entity(entity);
      builder.type(SerializableProvider.APPLICATION_SERIALIZABLE);
      builder.header(Validation.VALIDATION_HEADER, "true");
      return builder.build();
   }
}
