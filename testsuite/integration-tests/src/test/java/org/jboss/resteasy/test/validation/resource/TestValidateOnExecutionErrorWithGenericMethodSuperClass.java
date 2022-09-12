package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public class TestValidateOnExecutionErrorWithGenericMethodSuperClass<T> {
   @POST
   @Path("bar")
   @ValidateOnExecution(type = {ExecutableType.ALL})
   public void override(T qux) {
   }
}
