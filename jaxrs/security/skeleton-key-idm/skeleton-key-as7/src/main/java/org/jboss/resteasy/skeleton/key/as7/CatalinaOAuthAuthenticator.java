package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CatalinaOAuthAuthenticator
{
   protected CatalinaRealmConfiguration realmConfig;
   protected SkeletonKeyTokenVerification verification;

   public CatalinaOAuthAuthenticator(CatalinaRealmConfiguration realmConfig)
   {
      this.realmConfig = realmConfig;
   }

   public boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaOAuthLogin oAuthLogin = new CatalinaOAuthLogin(realmConfig, request, response);
      boolean login = oAuthLogin.login();
      if (login && oAuthLogin.isCodePresent()) // redirect without code
      {
         StringBuffer buf = request.getRequestURL().append("?").append(request.getQueryString());
         UriBuilder builder = UriBuilder.fromUri(buf.toString()).replaceQueryParam("code", null);
         try
         {
            response.sendRedirect(builder.build().toString());
            return false;
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else if (!login)
      {
         return false;
      }
      verification = oAuthLogin.getVerification();
      return true;
   }

   public CatalinaRealmConfiguration getRealmConfig()
   {
      return realmConfig;
   }

   public SkeletonKeyTokenVerification getVerification()
   {
      return verification;
   }
}
