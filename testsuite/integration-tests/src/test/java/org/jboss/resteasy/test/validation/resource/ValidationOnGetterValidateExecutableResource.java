package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("resource/executable")
@ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
public class ValidationOnGetterValidateExecutableResource {
    @Path("getter")
    @GET
    @Valid
    public ValidationOnGetterStringBean getStringBean() {
        return new ValidationOnGetterStringBean("1");
    }
}
