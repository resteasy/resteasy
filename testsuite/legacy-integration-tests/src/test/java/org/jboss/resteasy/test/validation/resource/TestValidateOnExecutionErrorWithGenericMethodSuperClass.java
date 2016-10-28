package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("")
public class TestValidateOnExecutionErrorWithGenericMethodSuperClass<T> {
    @POST
    @Path("bar")
    @ValidateOnExecution(type = {ExecutableType.ALL})
    public void override(T qux) {
    }
}
