package org.jboss.resteasy.skeleton.key.idm.service;

import org.jboss.resteasy.skeleton.key.idm.IdentityManager;
import org.jboss.resteasy.skeleton.key.idm.i18n.LogMessages;
import org.jboss.resteasy.skeleton.key.idm.i18n.Messages;
import org.jboss.resteasy.skeleton.key.idm.model.data.Realm;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class RealmResource
{
   protected IdentityManager identityManager;
   @Context
   protected UriInfo uriInfo;

   public RealmResource(IdentityManager identityManager)
   {
      this.identityManager = identityManager;
   }

   @GET
   @Path("realms/{realm}")
   @Produces("application/json")
   public PublishedRealmRepresentation getRealm(@PathParam("realm") String id)
   {
      Realm realm = identityManager.getRealm(id);
      if (realm == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.realmNotFound());
         throw new NotFoundException();
      }
      return realmRep(realm, uriInfo);
   }

   @GET
   @Path("realms/{realm}.html")
   @Produces("text/html")
   public String getRealmHtml(@PathParam("realm") String id)
   {
      Realm realm = identityManager.getRealm(id);
      if (realm == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.realmNotFound());
         throw new NotFoundException();
      }
      return realmHtml(realm);
   }

   private String realmHtml(Realm realm)
   {
      StringBuffer html = new StringBuffer();

      UriBuilder auth = uriInfo.getBaseUriBuilder();
      auth.path(TokenManagement.class)
              .path(TokenManagement.class, "requestAccessCode");
      String authUri = auth.build(realm.getId()).toString();

      UriBuilder code = uriInfo.getBaseUriBuilder();
      code.path(TokenManagement.class).path(TokenManagement.class, "accessRequest");
      String codeUri = code.build(realm.getId()).toString();

      UriBuilder grant = uriInfo.getBaseUriBuilder();
      grant.path(TokenManagement.class).path(TokenManagement.class, "accessTokenGrant");
      String grantUrl = grant.build(realm.getId()).toString();

      html.append("<html><body><h1> ").append(Messages.MESSAGES.realm()).append(": ").append(realm.getName()).append("</h1>");
      html.append("<p>").append(Messages.MESSAGES.auth()).append(": ").append(authUri).append("</p>");
      html.append("<p>").append(Messages.MESSAGES.code()).append(": ").append(codeUri).append("</p>");
      html.append("<p>").append(Messages.MESSAGES.grant()).append(": ").append(grantUrl).append("</p>");
      html.append("<p>").append(Messages.MESSAGES.publicKey()).append(": ").append(realm.getPublicKeyPem()).append("</p>");
      html.append("</body></html>");

      return html.toString();
   }


   @GET
   @Path("realms")
   @Produces("application/json")
   public Response getRealmsByName(@QueryParam("name") String name)
   {
      if (name == null) return Response.noContent().build();
      List<Realm> realms = identityManager.getRealmsByName(name);
      if (realms.size() == 0) return Response.noContent().build();

      List<PublishedRealmRepresentation> list = new ArrayList<PublishedRealmRepresentation>();
      for (Realm realm : realms)
      {
         list.add(realmRep(realm, uriInfo));
      }
      GenericEntity<List<PublishedRealmRepresentation>> entity = new GenericEntity<List<PublishedRealmRepresentation>>(list){};
      return Response.ok(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
   }

   @GET
   @Path("realms.html")
   @Produces("text/html")
   public String getRealmsByNameHtml(@QueryParam("name") String name)
   {
      if (name == null) return "<html><body><h1>"+Messages.MESSAGES.noRealmsWithThatName()+"</h1></body></html>";
      List<Realm> realms = identityManager.getRealmsByName(name);
      if (realms.size() == 0) return "<html><body><h1>"+Messages.MESSAGES.noRealmsWithThatName()+"</h1></body></html>";
      if (realms.size() == 1) return realmHtml(realms.get(0));

      StringBuffer html = new StringBuffer();
      html.append("<html><body><h1>"+Messages.MESSAGES.realms()+"</h1>");
      for (Realm realm : realms)
      {
         html.append("<p><a href=\"").append(uriInfo.getBaseUriBuilder().path("realms").path(realm.getId() + ".html"))
                 .append("\">").append(realm.getId()).append("</a></p>");
      }
      html.append("</body></html>");
      return html.toString();
   }


   public static PublishedRealmRepresentation realmRep(Realm realm, UriInfo uriInfo)
   {
      PublishedRealmRepresentation rep = new PublishedRealmRepresentation();
      rep.setRealm(realm.getName());
      rep.setSelf(uriInfo.getRequestUri().toString());
      rep.setPublicKeyPem(realm.getPublicKeyPem());

      UriBuilder auth = uriInfo.getBaseUriBuilder();
      auth.path(TokenManagement.class)
              .path(TokenManagement.class, "requestAccessCode");
      rep.setAuthorizationUrl(auth.build(realm.getId()).toString());

      UriBuilder code = uriInfo.getBaseUriBuilder();
      code.path(TokenManagement.class).path(TokenManagement.class, "accessRequest");
      rep.setCodeUrl(code.build(realm.getId()).toString());

      UriBuilder grant = uriInfo.getBaseUriBuilder();
      grant.path(TokenManagement.class).path(TokenManagement.class, "accessTokenGrant");
      String grantUrl = grant.build(realm.getId()).toString();
      rep.setGrantUrl(grantUrl);
      return rep;
   }
}
