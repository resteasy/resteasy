package org.jboss.resteasy.test.validation.ejb.resource;

import javax.ejb.Singleton;
import jakarta.ws.rs.Path;

@Singleton
@Path("singleton")
public class EJBParameterViolationsOnlySingletonResource implements EJBParameterViolationsOnlyResourceIntf
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
