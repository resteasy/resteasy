package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
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

   public String toString(Cookie cookie)
   {
      if (cookie == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      }
      StringBuilder b = new StringBuilder();
      b.append("$Version=").append(cookie.getVersion()).append(";");
      b.append(cookie.getName()).append('=');
      if (cookie.getValue() != null)
      {
         quote(b, cookie.getValue());
      }
      if (cookie.getPath() != null)
      {
         b.append(";").append("$Path").append("=");
         quote(b, cookie.getPath());
      }
      if (cookie.getDomain() != null)
      {
         b.append(";").append("$Domain").append("=");
         quote(b, cookie.getDomain());
      }
      return b.toString();
   }
   
   protected void quote(StringBuilder b, String value) {

      if (MediaTypeHeaderDelegate.quoted(value)) {
          b.append('"');
          b.append(value);
          b.append('"');
      } else {
          b.append(value);
      }
  }
}