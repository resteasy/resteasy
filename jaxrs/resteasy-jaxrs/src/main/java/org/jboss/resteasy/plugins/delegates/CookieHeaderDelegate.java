package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate;
import org.jboss.resteasy.util.CookieParser;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieHeaderDelegate implements RuntimeDelegate.HeaderDelegate<Cookie>
{

   public Cookie fromString(String value) throws IllegalArgumentException
   {
      return CookieParser.parseCookies(value).get(0);
   }

   public String toString(Cookie value)
   {
      StringBuffer buf = new StringBuffer();
      ServerCookie.appendCookieValue(buf, 0, value.getName(), value.getValue(), value.getPath(), value.getDomain(), null, -1, false);
      return buf.toString();
   }
}