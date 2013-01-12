package org.jboss.resteasy.skeleton.key.jaxrs;

import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.skeleton.key.AbstractOAuthClient;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.spi.ForbiddenException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper code to obtain oauth access tokens via browser redirects
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsOAuthClient extends AbstractOAuthClient
{
   public Response redirect(UriInfo uriInfo, String redirectUri)
   {
      String state = getStateCode();

      URI url = UriBuilder.fromUri(authUrl)
              .queryParam("client_id", clientId)
              .queryParam("redirect_uri", redirectUri)
              .queryParam("state", state)
              .build();
      NewCookie cookie = new NewCookie(stateCookieName, state, uriInfo.getBaseUri().getPath(), null, null, -1, true);
      return Response.status(302)
              .location(url)
              .cookie(cookie).build();
   }

   public String getBearerToken(UriInfo uriInfo, HttpHeaders headers) throws BadRequestException, InternalServerErrorException
   {
      Cookie stateCookie = headers.getCookies().get(stateCookieName);
      if (stateCookie == null) throw new BadRequestException(new Exception("state cookie not set"));;

      String state = uriInfo.getQueryParameters().getFirst("state");
      if (state == null) throw new BadRequestException(new Exception("state parameter was null"));
      if (!state.equals(stateCookie.getValue()))
      {
         throw new BadRequestException(new Exception("state parameter invalid"));
      }
      String code = uriInfo.getQueryParameters().getFirst("code");
      if (code == null) throw new BadRequestException(new Exception("code parameter was null"));
      return resolveBearerToken(code);
   }
}
