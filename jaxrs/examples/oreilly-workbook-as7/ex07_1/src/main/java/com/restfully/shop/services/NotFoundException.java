package com.restfully.shop.services;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NotFoundException extends RuntimeException
{
   public NotFoundException(String s)
   {
      super(s);
   }
}
