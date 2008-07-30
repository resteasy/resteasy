/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.exception;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * A OptimisticLockingExceptionMapper.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException>
{

   public Response toResponse(OptimisticLockException exception)
   {
      ResponseBuilder builder = Response.status(Status.CONFLICT);
      builder.entity(exception.getMessage()).type("text/plain");
      return builder.build();
   }

}
