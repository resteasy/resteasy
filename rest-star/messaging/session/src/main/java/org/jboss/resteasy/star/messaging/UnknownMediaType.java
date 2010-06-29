package org.jboss.resteasy.star.messaging;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UnknownMediaType extends RuntimeException
{
   public UnknownMediaType(String s)
   {
      super(s);
   }
}
