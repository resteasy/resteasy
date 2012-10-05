package org.jboss.resteasy.skeleton.key.client;

import org.jboss.resteasy.skeleton.key.keystone.model.Access;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface TokenVerifier
{
   @GET
   @Produces("application/json")
   @Path("{token}")
   Access get(@PathParam("token") String tokenId);
}
