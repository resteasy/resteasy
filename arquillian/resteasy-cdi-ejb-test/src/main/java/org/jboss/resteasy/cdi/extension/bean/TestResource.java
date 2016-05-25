package org.jboss.resteasy.cdi.extension.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.cdi.util.Utilities;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Path("/extension")
@RequestScoped
public class TestResource
{  
   @Inject @Boston BostonHolder holder;
   
   @POST
   @Path("boston")
   public Response setup()
   {
      System.out.println("holder: " + holder);
      boolean response = true;
      response &= Utilities.isBoston(holder.getClass());
      response &= holder.getLeaf() != null;
      response &= holder.getReader() != null;
      return response ? Response.ok().build() : Response.serverError().build();
   }
   
}
