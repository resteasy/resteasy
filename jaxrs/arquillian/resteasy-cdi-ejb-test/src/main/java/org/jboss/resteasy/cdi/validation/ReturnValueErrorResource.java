package org.jboss.resteasy.cdi.validation;

import javax.ejb.Local;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Local
@Path("return")
public interface ReturnValueErrorResource
{
   @GET
   @Path("test")
   @Min(5)
   @Max(10)
   public int test();
}