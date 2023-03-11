package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

public interface TestValidateOnExecutionInterface {
    @POST
    @Path("overrideInterface1")
    @ValidateOnExecution(type = { ExecutableType.NONE })
    void overrideInterface1(String s);
}
