package org.jboss.resteasy.resteasy1008;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * RESTEASY-1008
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jan 21, 2014
 */
public class TestSubResource
{
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("{subparam}")
   @Min(17)
   public int submethod(@Min(13) @PathParam("subparam") int subparam)
   {
      System.out.println("Subresource.this: " + this);
      System.out.println("Subresource.submethod() returning " + subparam);
      return subparam;
   }
}