package org.jboss.resteasy.skeleton.key.jaxrs;

import org.jboss.resteasy.skeleton.key.OAuthLogin;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsOAuthLogin extends OAuthLogin
{
   protected ContainerRequestContext request;
   protected SecurityContext securityContext;
   protected int sslRedirectPort = 443;
   protected List<NewCookie> cookies = new ArrayList<NewCookie>();

   public JaxrsOAuthLogin(RealmConfiguration realmInfo, ContainerRequestContext request, SecurityContext securityContext)
   {
      super(realmInfo);
      this.request = request;
      this.securityContext = securityContext;
   }

   public List<NewCookie> getCookies()
   {
      return cookies;
   }

   public void setSslRedirectPort(int sslRedirectPort)
   {
      this.sslRedirectPort = sslRedirectPort;
   }

   @Override
   protected String getRequestUrl()
   {
      return request.getUriInfo().getRequestUri().toString();
   }

   @Override
   protected boolean isRequestSecure()
   {
      return securityContext.isSecure();
   }

   @Override
   protected int getSslRedirectPort()
   {
      return sslRedirectPort;
   }

   @Override
   protected void sendError(int code)
   {
      request.abortWith(Response.status(code).cookie(cookies.toArray(new NewCookie[cookies.size()])).build());
   }

   @Override
   protected void sendRedirect(String url)
   {
      Response.ResponseBuilder builder = Response.status(302).location(URI.create(url));
      builder.cookie(cookies.toArray(new NewCookie[cookies.size()]));
      request.abortWith(builder.build());
   }

   @Override
   protected String getCookieValue(String cookie)
   {
      Cookie c = request.getCookies().get(cookie);
      if (c == null) return null;
      return c.getValue();
   }

   @Override
   protected void resetCookie(String cookieName)
   {
      NewCookie cookie = new NewCookie(cookieName, "", null, null, null, 0, false);
      cookies.add(cookie);
   }

   @Override
   protected String getCode()
   {
      return request.getUriInfo().getQueryParameters().getFirst("code");
   }

   @Override
   protected X509Certificate[] getCertificateChain()
   {
      return new X509Certificate[0];
   }

   @Override
   protected void setCookie(String name, String value, String domain, String path, boolean secure)
   {
      cookies.add(new NewCookie(name, value, path, domain, null, -1, secure));
   }
}
