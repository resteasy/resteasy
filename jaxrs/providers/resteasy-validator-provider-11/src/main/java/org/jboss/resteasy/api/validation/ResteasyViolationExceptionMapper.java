package org.jboss.resteasy.api.validation;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.GroupDefinitionException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.plugins.providers.SerializableProvider;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 31, 2012
 */
@Provider
public class ResteasyViolationExceptionMapper implements ExceptionMapper<ValidationException>
{
   public Response toResponse(ValidationException exception)
   {
      if (exception instanceof ConstraintDefinitionException)
      {
         return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
      }
      if (exception instanceof ConstraintDeclarationException)
      {
         return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
      }
      if (exception instanceof GroupDefinitionException)
      {
         return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
      }
      if (exception instanceof ResteasyViolationException)
      {
         ResteasyViolationException resteasyViolationException = ResteasyViolationException.class.cast(exception);
         Exception e = resteasyViolationException.getException();
         if (e != null)
         {
            return buildResponse(unwrapException(e), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
         }
         else if (resteasyViolationException.getReturnValueViolations().size() == 0)
         {
            return buildResponse(resteasyViolationException.toString(), MediaType.TEXT_PLAIN, Status.BAD_REQUEST);
         }
         else
         {
            return buildResponse(resteasyViolationException.toString(), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
         }
      }
      return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
   }
   
   protected Response buildResponse(Object entity, String mediaType, Status status)
   {
      ResponseBuilder builder =  Response.status(status).entity(entity);
      builder.type(MediaType.TEXT_PLAIN);
      builder.header(Validation.VALIDATION_HEADER, "true");
      return builder.build();
   }
   
   protected String unwrapException(Throwable t)
   {
      StringBuffer sb = new StringBuffer();
      doUnwrapException(sb, t);
      return sb.toString();
   }
   
   private void doUnwrapException(StringBuffer sb, Throwable t)
   {
      if (t == null)
      {
         return;
      }
      sb.append(t.toString());
      if (t.getCause() != null && t != t.getCause())
      {
         sb.append('[');
         doUnwrapException(sb, t.getCause());
         sb.append(']');
      }
   }
}
