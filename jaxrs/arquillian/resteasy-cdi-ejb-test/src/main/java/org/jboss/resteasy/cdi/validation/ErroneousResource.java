package org.jboss.resteasy.cdi.validation;

import javax.ejb.Local;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Local
@SumConstraint(min=3, max=17)
public interface ErroneousResource extends ResourceParent
{
   public abstract int getNumberOne();

   @Max(13)
   @Override
   public abstract int getNumberTwo();

   public abstract void setNumberOne(int one);

   public abstract void setNumberTwo(int two);
   
   @GET
   @Path("correct/test/{num}")
   public Response test(@Min(5) @Max(10) @PathParam("num") int num);
}