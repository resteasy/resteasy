package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 20, 2016
 */
public class ClientCookie extends NewCookie
{  
   public ClientCookie(String name, String value)
   {
      super(name, value, null, null, DEFAULT_VERSION, null, DEFAULT_MAX_AGE, null, false, false);
   }
   
   public ClientCookie(Cookie cookie)
   {
      super(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion(), null, DEFAULT_MAX_AGE, false);
   }
}
