package org.jboss.resteasy.test.asynch.resource;

@SuppressWarnings("serial")
public class AsyncFilterException extends RuntimeException
{

   public AsyncFilterException(final String message)
   {
      super(message);
   }

}
