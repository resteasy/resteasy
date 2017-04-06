package org.jboss.resteasy.keystone.as7;

import org.apache.catalina.connector.Request;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.jboss.security.web.ThreadContext;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class JBossWebAuthLoginModule extends AbstractServerLoginModule
{

   @Override
   public boolean login() throws LoginException
   {
      Request request = (Request) ThreadContext.get(Request.class.getName());
      HttpServletResponse response = (HttpServletResponse)ThreadContext.get(HttpServletResponse.class.getName());
      return login(request, response);
   }

   protected abstract boolean login(Request request, HttpServletResponse response)  throws LoginException;

}
