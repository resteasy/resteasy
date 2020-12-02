package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = {ExecutableType.NONE})
public interface TestValidationOnExecuteSubInterface {
}
