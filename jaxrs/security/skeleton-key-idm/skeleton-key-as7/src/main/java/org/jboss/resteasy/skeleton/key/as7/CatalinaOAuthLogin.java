package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.jboss.logging.Logger;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CatalinaOAuthLogin
{
   private static final Logger log = Logger.getLogger(CatalinaOAuthLogin.class);
   protected Request request;
   protected HttpServletResponse response;
   protected boolean codePresent;
   protected RealmConfiguration realmInfo;
   protected SkeletonKeyTokenVerification verification;

   public CatalinaOAuthLogin(RealmConfiguration realmInfo, Request request, HttpServletResponse response)
   {
      this.request = request;
      this.response = response;
      this.realmInfo = realmInfo;
   }

   public SkeletonKeyTokenVerification getVerification()
   {
      return verification;
   }

   public RealmConfiguration getRealmInfo()
   {
      return realmInfo;
   }

   protected String getDefaultCookiePath()
   {
      String path = request.getContextPath();
      if ("".equals(path) || path == null) path = "/";
      return path;
   }

   protected String getRequestUrl()
   {
      return request.getRequestURL().toString();
   }

   protected boolean isRequestSecure()
   {
      return request.isSecure();
   }

   protected int getSslRedirectPort()
   {
      return request.getConnector().getRedirectPort();
   }

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
         codePresent = true;
         return param.substring(eq + 1);
      }
      return null;
   }

   public boolean isCodePresent()
   {
      return codePresent;
   }

   protected X509Certificate[] getCertificateChain()
   {
      // disabled at the moment.  If verify-client is false, this method call will crap out the SSL connection
      if (true) return null;
      return request.getCertificateChain();
   }

   protected void setCookie(String name, String value, String domain, String path, boolean secure)
   {
      Cookie cookie = new Cookie(name, value);
      if (domain != null) cookie.setDomain(domain);
      if (path != null) cookie.setPath(path);
      if (secure) cookie.setSecure(true);
      response.addCookie(cookie);
   }

   protected String getRedirectUri(String state)
   {
      String url = getRequestUrl();
      if (!isRequestSecure() && realmInfo.isSslRequired())
      {
         int port = getSslRedirectPort();
         if (port < 0)
         {
            // disabled?
            return null;
         }
         UriBuilder secureUrl = UriBuilder.fromUri(url).scheme("https").port(-1);
         if (port != 443) secureUrl.port(port);
         url = secureUrl.build().toString();
      }
      return realmInfo.getAuthUrl().clone()
              .queryParam("client_id", realmInfo.getClientId())
              .queryParam("redirect_uri", url)
              .queryParam("state", state)
              .build().toString();
   }

   protected static final AtomicLong counter = new AtomicLong();

   protected String getStateCode()
   {
      return counter.getAndIncrement() + "/" + UUID.randomUUID().toString();
   }

   public void loginRedirect()
   {
      String state = getStateCode();
      String redirect = getRedirectUri(state);
      if (redirect == null)
      {
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return;
      }
      setCookie(realmInfo.getStateCookieName(), state, null, getDefaultCookiePath(), realmInfo.isSslRequired());
      sendRedirect(redirect);
   }

   public boolean checkStateCookie()
   {
      String stateCookie = getCookieValue(realmInfo.getStateCookieName());
      if (stateCookie == null)
      {
         sendError(400);
         log.warn("No state cookie");
         return false;
      }

      String state = request.getParameter("state");
      if (state == null)
      {
         sendError(400);
         log.warn("state parameter was null");
         return false;
      }
      if (!state.equals(stateCookie))
      {
         sendError(400);
         log.warn("state parameter invalid");
         log.warn("cookie: " + stateCookie);
         log.warn("queryParam: " + state);
         return false;
      }
      return true;

   }


   public boolean login()
   {
      String code = getCode();
      if (code == null)
      {
         log.info("There is no code, so redirect");
         loginRedirect();
         return false;
      }

      // abort if not HTTPS
      if (realmInfo.isSslRequired() && !isRequestSecure())
      {
         log.info("SSL is required");
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }

      if (!checkStateCookie()) return false;

      MultivaluedHashMap<String, String> creds = new MultivaluedHashMap<String, String>();
      creds.putAll(realmInfo.getCredentials().asMap());
      Form form = new Form(creds);
      form.param("grant_type", "authorization_code")
              .param("code", code)
              .param("redirect_uri", getRequestUrl())
              .param("client_id", realmInfo.getClientId());

      Response res = realmInfo.getCodeUrl().request().post(Entity.form(form));
      AccessTokenResponse tokenResponse;
      try
      {
         if (res.getStatus() != 200)
         {
            log.info("failed to turn code into token");
            sendError(Response.Status.FORBIDDEN.getStatusCode());
            return false;
         }
         log.info("media type: " + res.getMediaType());
         log.info("Content-Type header: " + res.getHeaderString("Content-Type"));
         tokenResponse = res.readEntity(AccessTokenResponse.class);
      }
      finally
      {
         res.close();
      }

      String tokenString = tokenResponse.getToken();
      X509Certificate[] chain = getCertificateChain();
      try
      {
         verification = RSATokenVerifier.verify(chain, tokenString, realmInfo.getMetadata());
         log.info("Verification succeeded!");
      }
      catch (VerificationException e)
      {
         log.info("failed verification of token");
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }
      log.info("Registering...");
      return true;
   }


}
