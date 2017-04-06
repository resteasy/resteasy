package org.jboss.resteasy.skeleton.key.jaxrs;

import org.jboss.resteasy.skeleton.key.AbstractOAuthClient;
import org.jboss.resteasy.skeleton.key.i18n.Messages;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.net.URI;

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
      String error = uriInfo.getQueryParameters().getFirst("error");
      if (error != null) throw new BadRequestException(new Exception(Messages.MESSAGES.oAuthError(error)));
      Cookie stateCookie = headers.getCookies().get(stateCookieName);
      if (stateCookie == null) throw new BadRequestException(new Exception(Messages.MESSAGES.stateCookieNotSet()));;

      String state = uriInfo.getQueryParameters().getFirst("state");
      if (state == null) throw new BadRequestException(new Exception(Messages.MESSAGES.stateParameterWasNull()));
      if (!state.equals(stateCookie.getValue()))
      {
         throw new BadRequestException(new Exception(Messages.MESSAGES.stateParameterInvalid()));
      }
      String code = uriInfo.getQueryParameters().getFirst("code");
      if (code == null) throw new BadRequestException(new Exception(Messages.MESSAGES.codeParameterWasNull()));
      return resolveBearerToken(uriInfo.getRequestUri().toString(), code);
   }
}
