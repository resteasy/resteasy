package org.jboss.resteasy.oauth.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;
import org.jboss.resteasy.spi.ForbiddenException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class AppResource
{
   protected PublishedRealmRepresentation realm;

   public AppResource(PublishedRealmRepresentation realm)
   {
      this.realm = realm;
   }

   @Context
   protected UriInfo uriInfo;

   @Context
   protected HttpHeaders headers;

   @Context
   protected SecurityContext securityContext;

   protected static final AtomicLong counter = new AtomicLong();

   protected String getStateCode()
   {
      return counter.getAndIncrement() + "/" + UUID.randomUUID().toString();
   }

   @Path("redirect")
   @GET
   public Response oauthRedirect()
   {
      String redirect = uriInfo.getBaseUriBuilder().path(AppResource.class).path(AppResource.class, "getData").build().toString();
      String client_id = "oauthclient";

      String state = getStateCode();

      URI authUrl = UriBuilder.fromUri(realm.getAuthorizationUrl())
              .queryParam("client_id", client_id)
              .queryParam("redirect_uri", redirect)
              .queryParam("state", state)
              .build();
      NewCookie cookie = new NewCookie("OAuth_State", state, uriInfo.getBaseUri().getPath(), null, null, -1, securityContext.isSecure());
      return Response.status(302)
              .location(authUrl)
              .cookie(cookie).build();
   }

   @Path("data")
   @GET
   @Produces("text/plain")
   public String getData()
   {
      String token = getBearerToken();

      ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
      String data = client.target("https://localhost:8443/application/user/users.txt")
              .request()
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
              .get(String.class);
      return "HERE'S THE DATA: \n" + data;
   }

   protected String getBearerToken()
   {
      Cookie stateCookie = headers.getCookies().get("OAuth_State");
      if (stateCookie == null) throw new ForbiddenException("State cookie not set");

      String state = uriInfo.getQueryParameters().getFirst("state");
      if (state == null) throw new ForbiddenException("State query parameter not set");
      String code = uriInfo.getQueryParameters().getFirst("code");
      if (code == null) throw new ForbiddenException("Code query parameter not set");
      ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
      Form codeForm = new Form()
              .param("code", code)
              .param("client_id", "oauthclient")
              .param("Password", "clientpassword");
      Response res = client.target(realm.getCodeUrl()).request().post(Entity.form(codeForm));
      if (res.getStatus() == 400)
      {
         throw new BadRequestException();

      }
      else if (res.getStatus() != 200)
      {
         throw new InternalServerErrorException();
      }
      AccessTokenResponse tokenResponse = res.readEntity(AccessTokenResponse.class);
      res.close();
      client.close();
      return tokenResponse.getToken();
   }
}
