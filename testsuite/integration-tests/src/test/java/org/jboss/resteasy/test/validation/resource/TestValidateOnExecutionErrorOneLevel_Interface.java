package org.jboss.resteasy.test.validation.resource;

import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
@ValidateOnExecution(type = {ExecutableType.NONE})
public class TestValidateOnExecutionErrorOneLevel_Interface implements TestValidateOnExecutionSubInterface {
   @POST
   @Path("overrideInterface1")
   @Override
   public void overrideInterface1(String s) {
   }

   @POST
   @Path("overrideInterface2")
   @ValidateOnExecution(type = {ExecutableType.ALL})
   @Override
   public void overrideInterface2(String s) {
   }
}
