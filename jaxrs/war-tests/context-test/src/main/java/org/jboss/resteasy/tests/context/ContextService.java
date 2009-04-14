package org.jboss.resteasy.tests.context;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class ContextService
{
   @GET
   @Path("/test")
   @Produces("text/plain")
   public String get(@Context UriInfo uri)
   {
      System.out.println(uri.getBaseUri());
      return uri.getBaseUri().toString();
   }

   @GET
   @Produces("text/plain")
   public String getBase(@Context UriInfo uri)
   {
      System.out.println(uri.getBaseUri());
      return uri.getBaseUri().toString();
   }
}
