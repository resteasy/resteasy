package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public class TestValidateOnExecutionErrorWithGenericMethodSubClass extends TestValidateOnExecutionErrorWithGenericMethodSuperClass<String> {
   @POST
   @Path("bar")
   @ValidateOnExecution(type = {ExecutableType.ALL})
   @Override
   public void override(String qux) {
   }
}
