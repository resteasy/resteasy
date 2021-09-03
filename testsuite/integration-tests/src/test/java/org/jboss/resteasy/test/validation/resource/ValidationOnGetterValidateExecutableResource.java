package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;
import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

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
