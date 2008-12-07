package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class NoResourceFoundFailure extends Failure
{

   public NoResourceFoundFailure(int errorCode)
   {
      super(errorCode);
   }

   public NoResourceFoundFailure(String s, int errorCode)
   {
      super(s, errorCode);
   }

   public NoResourceFoundFailure(String s, Response response)
   {
      super(s, response);
   }

   public NoResourceFoundFailure(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable, errorCode);
   }

   public NoResourceFoundFailure(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public NoResourceFoundFailure(String s, Throwable throwable)
   {
      super(s, throwable);
   }

   public NoResourceFoundFailure(String s)
   {
      super(s);
   }

   public NoResourceFoundFailure(Throwable throwable, int errorCode)
   {
      super(throwable, errorCode);
   }

   public NoResourceFoundFailure(Throwable throwable, Response response)
   {
      super(throwable, response);
   }

   public NoResourceFoundFailure(Throwable throwable)
   {
      super(throwable);
   }

}