package org.jboss.resteasy.skeleton.key;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class OAuthLogin
{
   protected RealmConfiguration realmInfo;
   protected SkeletonKeyTokenVerification verification;

   protected abstract String getRequestUrl();
   protected abstract boolean isRequestSecure();
   protected abstract int getSslRedirectPort();

   protected abstract void sendError(int code);
   protected abstract void sendRedirect(String url);
   protected abstract String getCookieValue(String cookie);
   protected abstract void resetCookie(String cookieName);
   protected abstract String getCode();
   protected abstract X509Certificate[] getCertificateChain();
   protected abstract void setCookie(String name, String value, String domain, String path, boolean secure);
   protected abstract String getDefaultCookiePath();

   private static final Logger logger = Logger.getLogger(OAuthLogin.class);


   protected OAuthLogin(RealmConfiguration realmInfo)
   {
      this.realmInfo = realmInfo;
   }

   public RealmConfiguration getRealmInfo()
   {
      return realmInfo;
   }

   public SkeletonKeyTokenVerification getVerification()
   {
      return verification;
   }


   protected boolean checkCookie()
   {
      String cookieName = realmInfo.getSessionCookieName();
      String id = getCookieValue(cookieName);
      if (id == null)
      {
         logger.info("Cookie not found: " + cookieName);
         return false;
      }
      verification = realmInfo.getVerification(id);
      if (verification == null)
      {
         logger.info("Invalid cookie: " + cookieName + " value: " + id);
         resetCookie(cookieName);
         return false;
      }
      return true;
   }

   protected void register()
   {
      realmInfo.register(verification.getToken().getId(), verification);
      String cookiePath = getDefaultCookiePath();
      if (realmInfo.getCookiePath() != null) cookiePath = realmInfo.getCookiePath();
      logger.info("Cookie Path: " + cookiePath);
      setCookie(realmInfo.getSessionCookieName(), verification.getToken().getId(), null, cookiePath, realmInfo.isCookieSecure());
   }



   protected String getRedirectUri()
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
      return realmInfo.getAuthUrl().clone().queryParam("client_id", realmInfo.getClientId()).queryParam("redirect_uri", url).build().toString();
   }

   public void loginRedirect()
   {
      String redirect = getRedirectUri();
      if (redirect == null)
      {
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return;
      }
      sendRedirect(redirect);
   }


   public boolean login()
   {
      logger.info("checking cookie");
      if (checkCookie()) return true;
      logger.info("no cookie");
      String code = getCode();
      if (code == null)
      {
         logger.info("There is no code, so redirect");
         loginRedirect();
         return false;
      }
      // abort if not HTTPS
      if (realmInfo.isSslRequired() && !isRequestSecure())
      {
         logger.info("SSL is required");
         sendError(Response.Status.FORBIDDEN.getStatusCode());
      }

      MultivaluedHashMap<String, String> creds = new MultivaluedHashMap<String, String>();
      creds.putAll(realmInfo.getCredentials().asMap());
      Form form = new Form(creds);
      form.param("grant_type", "authorization_code")
              .param("code", code)
              .param("redirect_uri", getRedirectUri())
              .param("client_id", realmInfo.getClientId());

      Response res = realmInfo.getCodeUrl().request().post(Entity.form(form));
      AccessTokenResponse tokenResponse;
      try
      {
         if (res.getStatus() != 200)
         {
            logger.info("failed to turn code into token");
            sendError(Response.Status.FORBIDDEN.getStatusCode());
            return false;
         }
         logger.info("media type: " + res.getMediaType());
         logger.info("Content-Type header: " + res.getHeaderString("Content-Type"));
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
         logger.info("Verification succeeded!");
      }
      catch (VerificationException e)
      {
         logger.info("failed verification of token");
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }
      logger.info("Registering...");
      register();
      return true;
   }
}
