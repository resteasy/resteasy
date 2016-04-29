package org.jboss.resteasy.test.nextgen.finegrain;

import javax.ws.rs.core.NewCookie;

import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.junit.Test;

/**
 * RESTEASY-1328
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class CookieNullValueTest
{
   @Test
   public void testCookie()
   {
      NewCookieHeaderDelegate delegate = new NewCookieHeaderDelegate();
      Object o = delegate.fromString("a=");
      System.out.println("toString(): " + ((NewCookie)o).toString());
   }
}
