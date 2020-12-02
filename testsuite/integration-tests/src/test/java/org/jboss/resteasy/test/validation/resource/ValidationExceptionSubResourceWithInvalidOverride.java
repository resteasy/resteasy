package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintDeclarationException;
import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;

@Path("/")
public class ValidationExceptionSubResourceWithInvalidOverride extends ValidationExceptionSuperResource {

   @Provider
   public static class ConstraintDeclarationExceptionMapper extends ValidationExceptionMapper<ConstraintDeclarationException> {
   }

   @POST
   public void test(@Size(max = 3) String s) {
   }
}
