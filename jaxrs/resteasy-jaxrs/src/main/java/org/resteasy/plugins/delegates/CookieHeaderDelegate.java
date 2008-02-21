package org.resteasy.plugins.delegates;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String value) throws IllegalArgumentException
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public String toString(Object value)
   {
      Cookie cookie = (Cookie) value;
      StringBuffer buf = new StringBuffer();
      ServerCookie.appendCookieValue(buf, 0, cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), null, -1, false);
      return buf.toString();
   }
}