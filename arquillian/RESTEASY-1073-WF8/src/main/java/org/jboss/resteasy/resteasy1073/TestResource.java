package org.jboss.resteasy.resteasy1073;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * RESTEASY-1073
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright July 19, 2014
 */
@Path("")
public class TestResource
{
   @POST
   @Path("test")
   @Consumes(MediaType.APPLICATION_XML)
   public String post(TestWrapper wrapper)
   {
      return wrapper.getName();
   }
}
