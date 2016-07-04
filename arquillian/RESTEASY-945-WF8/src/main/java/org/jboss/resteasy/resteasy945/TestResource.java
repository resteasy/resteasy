package org.jboss.resteasy.resteasy945;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * RESTEASY-945
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 14, 2014
 */
@Path("all")
@TestClassConstraint(5)
public class TestResource
{
   @Size(min=2, max=4)
   @PathParam("s")
   String s;

   private String t;

   @Size(min=3, max=5)  
   public String getT()
   {
      return t;
   }
   
   public String retrieveS()
   {
      return s;
   }

   @PathParam("t") 
   public void setT(String t)
   {
      this.t = t;
   }

   @GET
   @Path("{s}/{t}/{u}")
   @Size(max=3)
   @Produces(MediaType.TEXT_PLAIN)
   public String test(@Size(min=4, max=6) @PathParam("u") String u)
   {
      return s + t + u;
   }
}
