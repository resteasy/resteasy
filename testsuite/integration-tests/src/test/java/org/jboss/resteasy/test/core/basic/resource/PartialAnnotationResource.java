package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
