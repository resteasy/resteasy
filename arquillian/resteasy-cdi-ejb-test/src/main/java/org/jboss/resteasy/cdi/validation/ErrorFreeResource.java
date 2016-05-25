package org.jboss.resteasy.cdi.validation;

import javax.ejb.Local;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Local
@Path("correct")
@SumConstraint(min=3, max=27)
public interface ErrorFreeResource extends ResourceParent
{
   @GET
   @Path("test/{num}")
   @Max(10)
   @Produces("text/plain")
   public int test(@Min(5) @Max(10) @PathParam("num") int num);
   
   public abstract int getNumberOne();

   public abstract int getNumberTwo();

   public abstract void setNumberOne(int one);

   public abstract void setNumberTwo(int two);
}