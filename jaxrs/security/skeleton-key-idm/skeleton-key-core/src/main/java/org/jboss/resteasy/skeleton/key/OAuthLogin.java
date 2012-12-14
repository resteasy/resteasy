package org.jboss.resteasy.skeleton.key;

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

   private static final String LOGIN_COOKIE = "SKELETON_KEY_LOGIN";

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

   protected String getCookieName()
   {
      return LOGIN_COOKIE + "." + realmInfo.getMetadata().getRealm() + "." + realmInfo.getMetadata().getResourceName();
   }

   protected boolean checkCookie()
   {
      String cookieName = getCookieName();
      String id = getCookieValue(cookieName);
      verification = realmInfo.getVerification(id);
      if (verification == null)
      {
         resetCookie(cookieName);
         return false;
      }
      return true;
   }

   protected void register()
   {
      realmInfo.register(verification.getToken().getId(), verification);
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

   protected void loginRedirect()
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
      if (checkCookie()) return true;
      String code = getCode();
      if (code == null)
      {
         loginRedirect();
         return false;
      }
      // abort if not HTTPS
      if (realmInfo.isSslRequired() && !isRequestSecure())
      {
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
      if (res.getStatus() != 200)
      {
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }
      AccessTokenResponse tokenResponse = res.readEntity(AccessTokenResponse.class);

      String tokenString = tokenResponse.getToken();
      X509Certificate[] chain = getCertificateChain();
      try
      {
         verification = RSATokenVerifier.verify(chain, tokenString, realmInfo.getMetadata());
      }
      catch (VerificationException e)
      {
         sendError(Response.Status.FORBIDDEN.getStatusCode());
         return false;
      }
      register();
      setCookie(getCookieName(), verification.getToken().getId(), null, realmInfo.getCookiePath(), realmInfo.isCookieSecure());
      return true;
   }
}
