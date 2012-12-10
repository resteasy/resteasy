package org.jboss.resteasy.skeleton.key.service;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.skeleton.key.IdentityManager;
import org.jboss.resteasy.skeleton.key.model.data.Realm;
import org.jboss.resteasy.skeleton.key.model.representations.PublishedRealmRepresentation;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/realms")
public class RealmResource
{
   protected Logger logger = Logger.getLogger(RealmResource.class);
   protected IdentityManager identityManager;
   @Context
   protected UriInfo uriInfo;

   public RealmResource(IdentityManager identityManager)
   {
      this.identityManager = identityManager;
   }

   @GET
   @Path("{realm}")
   @Produces("application/json")
   public PublishedRealmRepresentation getRealm(@PathParam("realm") String id)
   {
      Realm realm = identityManager.getRealm(id);
      if (realm == null)
      {
         logger.debug("realm not found");
         throw new NotFoundException();
      }

      UriInfo uriInfo = this.uriInfo;

      return realmRep(realm, uriInfo);
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
