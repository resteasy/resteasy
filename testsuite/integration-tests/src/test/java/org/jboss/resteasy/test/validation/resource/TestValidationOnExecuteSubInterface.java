package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = {ExecutableType.NONE})
public interface TestValidationOnExecuteSubInterface {
}
