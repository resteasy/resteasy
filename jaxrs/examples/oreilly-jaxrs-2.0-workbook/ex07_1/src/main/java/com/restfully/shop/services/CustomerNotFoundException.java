package com.restfully.shop.services;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerNotFoundException extends RuntimeException
{
   public CustomerNotFoundException(String s)
   {
      super(s);
   }
}
