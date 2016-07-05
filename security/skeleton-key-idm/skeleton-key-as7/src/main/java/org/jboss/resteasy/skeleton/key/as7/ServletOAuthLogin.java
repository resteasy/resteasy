package org.jboss.resteasy.skeleton.key.as7;

import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.jboss.resteasy.skeleton.key.as7.i18n.LogMessages;
import org.jboss.resteasy.skeleton.key.as7.i18n.Messages;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.util.BasicAuthHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletOAuthLogin
{
   protected HttpServletRequest request;
   protected HttpServletResponse response;
   protected boolean codePresent;
   protected RealmConfiguration realmInfo;
   protected int redirectPort;
   protected String tokenString;
   protected SkeletonKeyToken token;

   public ServletOAuthLogin(RealmConfiguration realmInfo, HttpServletRequest request, HttpServletResponse response, int redirectPort)
   {
      this.request = request;
      this.response = response;
      this.realmInfo = realmInfo;
      this.redirectPort = redirectPort;
   }

   public String getTokenString()
   {
      return tokenString;
   }

   public SkeletonKeyToken getToken()
   {
      return token;
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

   protected Cookie getCookie(String cookieName)
   {
      if (request.getCookies() == null) return null;
      for (Cookie cookie : request.getCookies())
      {
         if (cookie.getName().equals(cookieName))
         {
            return cookie;
         }
      }
      return null;
   }

   protected String getCookieValue(String cookieName)
   {
      Cookie cookie = getCookie(cookieName);
      if (cookie == null) return null;
      return cookie.getValue();
   }

   protected String getQueryParamValue(String paramName)
   {
      String query = request.getQueryString();
      if (query == null) return null;
      String[] params = query.split("&");
      for (String param : params)
      {
         int eq = param.indexOf('=');
         if (eq == -1) continue;
         String name = param.substring(0, eq);
         if (!name.equals(paramName)) continue;
         return param.substring(eq + 1);
      }
      return null;
   }

   public String getError()
   {
      return getQueryParamValue("error");
   }

   public String getCode()
   {
      return getQueryParamValue("code");
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
         int port = redirectPort;
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
              .queryParam("login", "true")
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
      Cookie stateCookie = getCookie(realmInfo.getStateCookieName());

      if (stateCookie == null)
      {
         sendError(400);
         LogMessages.LOGGER.warn(Messages.MESSAGES.noStateCookie());
         return false;
      }
      // reset the cookie
      Cookie reset = new Cookie(stateCookie.getName(), stateCookie.getValue());
      reset.setPath(stateCookie.getPath());
      reset.setMaxAge(0);
      response.addCookie(reset);

      String stateCookieValue = getCookieValue(realmInfo.getStateCookieName());
      // its ok to call request.getParameter() because this should be a redirect
      String state = request.getParameter("state");
      if (state == null)
      {
         sendError(400);
         LogMessages.LOGGER.warn(Messages.MESSAGES.stateParameterWasNull());
         return false;
      }
      if (!state.equals(stateCookieValue))
      {
         sendError(400);
         LogMessages.LOGGER.warn(Messages.MESSAGES.stateParameterInvalid());
         LogMessages.LOGGER.warn(Messages.MESSAGES.cookie(stateCookieValue));
         LogMessages.LOGGER.warn(Messages.MESSAGES.queryParam(state));
         return false;
      }
      return true;

   }

   /**
    * Start or continue the oauth login process.
    *
    * if code query parameter is not present, then browser is redirected to authUrl.  The redirect URL will be
    * the URL of the current request.
    *
    * If code query parameter is present, then an access token is obtained by invoking a secure request to the codeUrl.
    * If the access token is obtained, the browser is again redirected to the current request URL, but any OAuth
    * protocol specific query parameters are removed.
    *
    * @return true if an access token was obtained
    */
   public boolean resolveCode(String code)
   {
      // abort if not HTTPS
      if (realmInfo.isSslRequired() && !isRequestSecure())
      {
         LogMessages.LOGGER.error(Messages.MESSAGES.sslIsRequired());
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }

      if (!checkStateCookie()) return false;

      String client_id = realmInfo.getClientId();
      String password = realmInfo.getCredentials().asMap().getFirst("password");
      String authHeader = BasicAuthHelper.createHeader(client_id, password);
      String redirectUri = stripOauthParametersFromRedirect();
      Form form = new Form();
      form.param("grant_type", "authorization_code")
              .param("code", code)
              .param("redirect_uri", redirectUri);

      Response res = realmInfo.getCodeUrl().request().header(HttpHeaders.AUTHORIZATION, authHeader).post(Entity.form(form));
      AccessTokenResponse tokenResponse;
      try
      {
         if (res.getStatus() != 200)
         {
            LogMessages.LOGGER.error(Messages.MESSAGES.failedToTurnCodeIntoToken());
            sendError(Response.Status.FORBIDDEN.getStatusCode());
            return false;
         }
         LogMessages.LOGGER.debug(Messages.MESSAGES.mediaType(res.getMediaType()));
         LogMessages.LOGGER.debug(Messages.MESSAGES.contentTypeHeader(res.getHeaderString("Content-Type")));
         tokenResponse = res.readEntity(AccessTokenResponse.class);
      }
      finally
      {
         res.close();
      }

      tokenString = tokenResponse.getToken();
      try
      {
         token = RSATokenVerifier.verifyToken(tokenString, realmInfo.getMetadata());
         LogMessages.LOGGER.debug(Messages.MESSAGES.verificationSucceeded());
      }
      catch (VerificationException e)
      {
         LogMessages.LOGGER.error(Messages.MESSAGES.failedVerificationOfToken());
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }
      // redirect to URL without oauth query parameters
      sendRedirect(redirectUri);
      return true;
   }

   /**
    * strip out unwanted query parameters and redirect so bookmarks don't retain oauth protocol bits
    */
   protected String stripOauthParametersFromRedirect()
   {
      StringBuffer buf = request.getRequestURL().append("?").append(request.getQueryString());
      UriBuilder builder = UriBuilder.fromUri(buf.toString())
              .replaceQueryParam("code", null)
              .replaceQueryParam("state", null);
      return builder.build().toString();
   }


}
