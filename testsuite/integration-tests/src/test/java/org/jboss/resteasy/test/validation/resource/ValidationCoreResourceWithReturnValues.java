package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/return")
public class ValidationCoreResourceWithReturnValues {
   @POST
   @Path("/native")
   @Valid
   public ValidationCoreFoo postNative(ValidationCoreFoo foo) {
      return foo;
   }

   @POST
   @Path("/imposed")
   @ValidationCoreFooConstraint(min = 3, max = 5)
   public ValidationCoreFoo postImposed(ValidationCoreFoo foo) {
      return foo;
   }

   @POST
   @Path("nativeAndImposed")
   @Valid
   @ValidationCoreFooConstraint(min = 3, max = 5)
   public ValidationCoreFoo postNativeAndImposed(ValidationCoreFoo foo) {
      return foo;
   }
}
