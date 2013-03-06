package org.jboss.resteasy.resteasy802;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("test")
   public Response test()
   {
      return Response.ok("test").build();
   }

}
