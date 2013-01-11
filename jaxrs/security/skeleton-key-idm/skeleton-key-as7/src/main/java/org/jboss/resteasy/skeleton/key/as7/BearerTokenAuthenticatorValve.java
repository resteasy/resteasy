package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.jboss.logging.Logger;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Uses a configured remote auth server to do Bearer token authentication only.  SkeletonKeyTokens are used
 * to provide user data and role mappings.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BearerTokenAuthenticatorValve extends AbstractRemoteOAuthAuthenticatorValve
{
   private static final Logger log = Logger.getLogger(BearerTokenAuthenticatorValve.class);


   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      try
      {
         super.invoke(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData(); // to clear push of SkeletonKeySession
      }
   }

   @Override
   protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      try
      {
         CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(resourceMetadata, !remoteSkeletonKeyConfig.isCancelPropagation(), true);
         if (bearer.login(request, response))
         {
            return true;
       }
         return false;
      }
      catch (LoginException e)
      {
      }
      return false;
   }
}
