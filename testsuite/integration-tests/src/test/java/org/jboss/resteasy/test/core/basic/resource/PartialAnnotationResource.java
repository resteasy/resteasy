package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * A PartialAnnotationResource.
 *
 * @author pjurak
 */
@Path("/test")
public interface PartialAnnotationResource
{
   @GET
   @Produces("text/plain")
   String bar();

   /** This is not REST method.
    *
    * @return
    */
   // @GET
   // @Produces("text/plain")
   String foo();
}
