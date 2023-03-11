package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.constraints.Size;
import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = { ExecutableType.NONE })
public class TestValidateOnExecutionErrorOneLevel_Class extends TestValidateOnExecutionResource {
    @POST
    @Path("override")
    @Size(min = 1)
    @Override
    @ValidateOnExecution(type = { ExecutableType.IMPLICIT })
    public String override(@Size(max = 1) String s) {
        return s;
    }
}
