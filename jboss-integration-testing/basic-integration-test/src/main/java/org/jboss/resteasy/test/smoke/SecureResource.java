package org.jboss.resteasy.test.smoke;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/security")
public class SecureResource
{

   @GET
   public String getSecure(@Context SecurityContext ctx)
   {
      System.out.println("********* IN SECURE CLIENT");
      if (!ctx.isUserInRole("admin"))
      {
          System.out.println("NOT IN ROLE!!!!");
          throw new WebApplicationException(401);
      }
      return "Wild";
   }
}