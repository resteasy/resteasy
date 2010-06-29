package org.jboss.resteasy.star.messaging;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UnmarshalException extends RuntimeException
{
   public UnmarshalException(String s)
   {
      super(s);
   }

   public UnmarshalException(String s, Throwable throwable)
   {
      super(s, throwable);
   }
}
