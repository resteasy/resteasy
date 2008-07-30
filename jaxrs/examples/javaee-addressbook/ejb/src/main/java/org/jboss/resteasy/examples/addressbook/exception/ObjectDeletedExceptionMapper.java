/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.hibernate.ObjectDeletedException;

/**
 * A ObjectDeletedExceptionMapper.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
public class ObjectDeletedExceptionMapper implements ExceptionMapper<ObjectDeletedException>
{

   /**
    * 
    */
   public Response toResponse(ObjectDeletedException exception)
   {
      ResponseBuilder builder = Response.status(Status.GONE);
      builder.entity(exception.getMessage()).type("text/plain");
      return builder.build();
   }

}
