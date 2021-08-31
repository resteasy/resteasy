package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * RESTEASY-1058
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *          <p>
 *          Copyright May 13, 2014
 */
@Path("/")
@MultipleWarSumConstraint(min = 9)
public class MultipleWarResource {
   @Min(3)
   @PathParam("field")
   protected int field;

   private int property;

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Max(0)
   @Path("test/{field}/{property}/{param}")
   public int test(@Min(7) @PathParam("param") int param) throws InterruptedException {
      return param;
   }

   @Min(5)
   public int getProperty() {
      return property;
   }

   @PathParam("property")
   public void setProperty(int property) {
      this.property = property;
   }
}
