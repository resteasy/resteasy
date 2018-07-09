package org.jboss.resteasy.test.asynch.resource;

@SuppressWarnings("serial")
public class AsyncFilterException extends RuntimeException
{

   public AsyncFilterException(String message)
   {
      super(message);
   }

}
