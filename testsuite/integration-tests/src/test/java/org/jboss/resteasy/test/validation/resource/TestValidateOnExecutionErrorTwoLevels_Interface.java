package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = {ExecutableType.NONE})
public class TestValidateOnExecutionErrorTwoLevels_Interface implements TestValidateOnExecutionSubInterface {
    @POST
    @Path("overrideInterface1")
    @ValidateOnExecution(type = {ExecutableType.ALL})
    @Override
    public void overrideInterface1(String s) {
    }

    @POST
    @Path("overrideInterface2")
    @Override
    public void overrideInterface2(String s) {
    }
}
