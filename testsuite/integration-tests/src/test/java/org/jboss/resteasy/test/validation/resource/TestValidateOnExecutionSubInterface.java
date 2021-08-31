package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

public interface TestValidateOnExecutionSubInterface extends
      TestValidateOnExecutionInterface {
   @POST
   @Path("overrideInterface2")
   @ValidateOnExecution(type = {ExecutableType.NONE})
   void overrideInterface2(String s);
}
