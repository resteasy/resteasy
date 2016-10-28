package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("")
public interface TestValidateOnExecutionInterfaceWithGenericSupermethod<T> {
    @POST
    @Path("bar")
    @ValidateOnExecution(type = {ExecutableType.NONE})
    void override(T qux);
}
