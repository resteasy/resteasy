package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
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
