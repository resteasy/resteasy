package org.jboss.resteasy.keystone.client;

import org.jboss.resteasy.keystone.model.Access;
import org.jboss.resteasy.keystone.model.Authentication;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface TokenFactory
{
   @POST
   @Consumes("application/json")
   @Produces("application/json")
   Access create(Authentication auth);

   @POST
   @Path("signed")
   @Consumes("application/json")
   @Produces("text/plain")
   String createSigned(Authentication auth);
}
