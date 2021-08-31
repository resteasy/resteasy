package org.jboss.resteasy.test.validation.ejb.resource;

import javax.ejb.Stateful;
import jakarta.ws.rs.Path;

@Stateful
@Path("stateful")
public class EJBParameterViolationsOnlyStatefulResource implements EJBParameterViolationsOnlyResourceIntf
{
   private static boolean executionFlag;

   @Override
   public String testValidation(EJBParameterViolationsOnlyDataObject payload) {
      executionFlag = true;
      return payload.getDirection();
   }

   @Override
   public boolean used() {
      return executionFlag;
   }

   @Override
   public void reset() {
      executionFlag = false;
   }
}
