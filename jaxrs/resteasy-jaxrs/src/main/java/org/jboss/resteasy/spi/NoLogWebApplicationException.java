package org.jboss.resteasy.spi;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * WebApplicationExceptions are logged by RESTEasy.  Use this exception when you don't want your exception logged
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NoLogWebApplicationException extends WebApplicationException
{
   public NoLogWebApplicationException()
   {
   }

   public NoLogWebApplicationException(Response response)
   {
      super(response);
   }

   public NoLogWebApplicationException(int status)
   {
      super(status);
   }

   public NoLogWebApplicationException(Response.Status status)
   {
      super(status);
   }

   public NoLogWebApplicationException(Throwable cause)
   {
      super(cause);
   }

   public NoLogWebApplicationException(Throwable cause, Response response)
   {
      super(cause, response);
   }

   public NoLogWebApplicationException(Throwable cause, int status)
   {
      super(cause, status);
   }

   public NoLogWebApplicationException(Throwable cause, Response.Status status)
   {
      super(cause, status);
   }
}
