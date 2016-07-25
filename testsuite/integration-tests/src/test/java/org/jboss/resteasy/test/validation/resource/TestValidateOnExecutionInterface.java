package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public interface TestValidateOnExecutionInterface {
    @POST
    @Path("overrideInterface1")
    @ValidateOnExecution(type = {ExecutableType.NONE})
    void overrideInterface1(String s);
}
