package org.jboss.resteasy.spi.validation;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Apr 3, 2012
 * 
 * @TODO: Work out representation of exceptions for client.
 */
public class ViolationUtils
{
   public static String getMessage(String violation)
   {
      return violation.substring(0, violation.indexOf(';'));
   }
   
   public static String getInvalidObject(String violation)
   {
      return violation.substring(violation.indexOf("; ") + 2);
   }
}
