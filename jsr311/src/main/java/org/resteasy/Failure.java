package org.resteasy;

import org.resteasy.util.HttpResponseCodes;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Failure extends RuntimeException
{
   private int errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;

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
}
