package org.jboss.security.web;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DomainDelegatingAuthenticator extends AuthenticatorBase
{
   private static Logger log = Logger.getLogger(DomainDelegatingAuthenticator.class.getName());
   @Override
   protected boolean authenticate(Request request, HttpServletResponse httpServletResponse, LoginConfig loginConfig) throws IOException
   {
      ThreadContext.set(Request.class.getName(), request);
      ThreadContext.set(HttpServletResponse.class.getName(), httpServletResponse);
      try
      {
         Principal principal = context.getRealm().authenticate("", "");

         if (principal == null)
         {
            return false;
         }
         else
         {
            request.setUserPrincipal(principal);
         }
      }
      finally
      {
         ThreadContext.clear();
      }
      return true;

   }


}
