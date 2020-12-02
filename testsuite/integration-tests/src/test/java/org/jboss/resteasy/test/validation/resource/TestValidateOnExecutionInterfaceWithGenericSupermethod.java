package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public interface TestValidateOnExecutionInterfaceWithGenericSupermethod<T> {
   @POST
   @Path("bar")
   @ValidateOnExecution(type = {ExecutableType.NONE})
   void override(T qux);
}
