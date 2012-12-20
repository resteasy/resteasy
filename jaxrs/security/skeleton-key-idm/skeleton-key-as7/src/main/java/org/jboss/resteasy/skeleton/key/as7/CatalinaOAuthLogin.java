package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.jboss.resteasy.skeleton.key.OAuthLogin;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CatalinaOAuthLogin extends OAuthLogin
{
   protected Request request;
   protected HttpServletResponse response;

   public CatalinaOAuthLogin(RealmConfiguration realmInfo, Request request, HttpServletResponse response)
   {
      super(realmInfo);
      this.request = request;
      this.response = response;
   }

   @Override
   protected String getRequestUrl()
   {
      return request.getRequestURL().toString();
   }

   @Override
   protected boolean isRequestSecure()
   {
      return request.isSecure();
   }

   @Override
   protected int getSslRedirectPort()
   {
      return request.getConnector().getRedirectPort();
   }

   @Override
   protected void sendError(int code)
   {
      try
      {
         response.sendError(code);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   protected void sendRedirect(String url)
   {
      try
      {
         response.sendRedirect(url);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   protected String getCookieValue(String cookieName)
   {
      if (request.getCookies() == null) return null;
      for (Cookie cookie : request.getCookies())
      {
         if (cookie.getName().equals(cookieName))
         {
            return cookie.getValue();
         }
      }
      return null;
   }

   @Override
   protected void resetCookie(String cookieName)
   {
      Cookie cookie = new Cookie(cookieName, "");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
   }

   @Override
   protected String getCode()
   {
      String query = request.getQueryString();
      if (query == null) return null;
      String[] params = query.split("&");
      for (String param : params)
      {
         int eq = param.indexOf('=');
         if (eq == -1) continue;
         String name = param.substring(0, eq);
         if (!name.equals("code")) continue;
         return param.substring(eq + 1);
      }
      return null;
   }

   @Override
   protected X509Certificate[] getCertificateChain()
   {
      return request.getCertificateChain();
   }

   @Override
   protected void setCookie(String name, String value, String domain, String path, boolean secure)
   {
      Cookie cookie = new Cookie(name, value);
      if (domain != null) cookie.setDomain(domain);
      if (path != null) cookie.setPath(path);
      if (secure) cookie.setSecure(true);
      response.addCookie(cookie);
   }

   @Override
   protected void register()
   {
      Session session = request.getSessionInternal(true);
      CatalinaRealmConfiguration cacheEntry = (CatalinaRealmConfiguration)realmInfo;
      cacheEntry.register(session, verification);
   }
}
