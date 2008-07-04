package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NewCookieHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String value) throws IllegalArgumentException
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public String toString(Object value)
   {
      NewCookie cookie = (NewCookie) value;
      StringBuffer buf = new StringBuffer();
      ServerCookie.appendCookieValue(buf, 0, cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getComment(), cookie.getMaxAge(), cookie.isSecure());
      return buf.toString();
   }
}
