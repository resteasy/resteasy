package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = {ExecutableType.NONE})
public class TestValidateOnExecutionResource implements TestValidationOnExecuteSubInterface {
    @POST
    @Path("none")
    @Size(min = 1)
    public String none(@Size(max = 1) String s) {
        return s;
    }

    @POST
    @Path("getterOnNonGetter")
    @Size(min = 1)
    @ValidateOnExecution(type = {ExecutableType.GETTER_METHODS, ExecutableType.CONSTRUCTORS, ExecutableType.NONE})
    public String nongetter1(@Size(max = 1) String s) {
        return s;
    }

    @POST
    @Path("nonGetterOnGetter")
    @Size(min = 1)
    @ValidateOnExecution(type = {ExecutableType.NON_GETTER_METHODS, ExecutableType.CONSTRUCTORS, ExecutableType.NONE})
    public String getS1() {
        return "abc";
    }

    @POST
    @Path("implicitOnNonGetter")
    @Size(min = 1)
    @ValidateOnExecution(type = {ExecutableType.IMPLICIT})
    public String nongetter2(@Size(max = 1) String s) {
        return s;
    }

    @POST
    @Path("implicitOnGetter")
    @Size(max = 1)
    @ValidateOnExecution(type = {ExecutableType.IMPLICIT})
    // Will be validated when other methods are called, returning a property
    // violation.
    public String getS2() {
        return "abc";
    }

    @POST
    @Path("allOnNonGetter")
    @Size(min = 1)
    @ValidateOnExecution(type = {ExecutableType.ALL})
    public String nongetter3(@Size(max = 1) String s) {
        return s;
    }

    @POST
    @Path("allOnGetter")
    @Size(max = 1)
    @ValidateOnExecution(type = {ExecutableType.ALL})
    // Will be validated when other methods are called, returning a property
    // violation.
    public String getS3() {
        return "abc";
    }

    @POST
    @Path("override")
    @Size(min = 1)
    @ValidateOnExecution(type = {ExecutableType.ALL})
    public String override(@Size(max = 1) String s) {
        return s;
    }
}
