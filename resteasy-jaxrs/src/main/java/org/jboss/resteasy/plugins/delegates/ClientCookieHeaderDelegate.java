package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.NewCookie;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 20, 2016
 */
public class ClientCookieHeaderDelegate extends NewCookieHeaderDelegate
{
   public String toString(Object value)
   {
      if (value == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      }
      NewCookie cookie = (NewCookie) value;
      StringBuilder b = new StringBuilder();
      b.append(cookie.getName()).append('=');
      if (cookie.getValue() != null)
      {
         quote(b, cookie.getValue());
      }
      return b.toString();
   }
}
