package org.jboss.resteasy.test.validation.ejb.resource;

import javax.ejb.Stateless;
import jakarta.ws.rs.Path;

@Stateless
@Path("stateless")
public class EJBParameterViolationsOnlyStatelessResource implements EJBParameterViolationsOnlyResourceIntf
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
