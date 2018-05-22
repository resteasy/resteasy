package org.jboss.resteasy.spi;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.Response;


/**
 * This exception should only be used by Resteasy integrators.  Applications code should use WebApplicationException.
 * <p>
 * This is thrown by Restasy runtime when a failure occurs.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Failure extends RuntimeException
{

   protected int errorCode = -1;
   protected boolean loggable;
   protected Response response;

   public Failure(String s, Response response)
   {
      super(s);
      this.response = response;
   }

   public Failure(String s, Throwable throwable, Response response)
   {
      super(s, throwable);
      this.response = response;
   }

   public Failure(Throwable throwable, Response response)
   {
      super(throwable);
      this.response = response;
   }

   public Failure(String s, Throwable throwable)
   {
      super(s, throwable);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(Throwable throwable)
   {
      super(throwable);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(String s)
   {
      super(s);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(int errorCode)
   {
      this.errorCode = errorCode;
   }

   public Failure(String s, int errorCode)
   {
      super(s);
      this.errorCode = errorCode;
   }

   public Failure(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable);
      this.errorCode = errorCode;
   }

   public Failure(Throwable throwable, int errorCode)
   {
      super(throwable);
      this.errorCode = errorCode;
   }

   public int getErrorCode()
   {
      return errorCode;
   }

   public void setErrorCode(int errorCode)
   {
      this.errorCode = errorCode;
   }

   public boolean isLoggable()
   {
      return loggable;
   }

   public void setLoggable(boolean loggable)
   {
      this.loggable = loggable;
   }

   public Response getResponse()
   {
      return response;
   }

   public void setResponse(Response response)
   {
      this.response = response;
   }
}
