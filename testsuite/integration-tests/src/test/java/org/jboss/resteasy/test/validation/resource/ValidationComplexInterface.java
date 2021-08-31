package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public interface ValidationComplexInterface {
   @Path("/inherit")
   @POST
   @Size(min = 2, max = 3)
   String postInherit(@Size(min = 2, max = 4) String s);

   @Path("/override")
   @POST
   @Size(min = 2, max = 3)
   String postOverride(@Size(min = 2, max = 4) String s);
}
