/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.exception;

import javax.ejb.EJBException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * A EJBExceptionMapper.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException>
{

   public Response toResponse(EJBException exception)
   {
      if(exception.getCause() instanceof WebApplicationException) {
         WebApplicationException webex = (WebApplicationException) exception.getCause();
         return webex.getResponse();
      }
      ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
      builder.entity(exception.getMessage()).type("text/plain");
      return builder.build();
   }

}
