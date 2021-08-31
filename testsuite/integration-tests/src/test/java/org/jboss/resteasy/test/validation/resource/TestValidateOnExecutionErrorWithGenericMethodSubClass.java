package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
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
