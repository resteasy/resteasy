package org.jboss.resteasy.tests.scanning;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyResourceImpl implements MyResource
{
   public Subresource doit()
   {
      return new Subresource();
   }

   public String get()
   {
      return "hello world";
   }
}
