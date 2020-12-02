package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintDefinitionException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;

@Path("/")
@ValidationExceptionIncorrectConstraint
public class ValidationExceptionResourceWithIncorrectConstraint {

   @Provider
   public static class ConstraintDefinitionExceptionMapper extends ValidationExceptionMapper<ConstraintDefinitionException> {
   }

   @POST
   public void test() {
   }
}
