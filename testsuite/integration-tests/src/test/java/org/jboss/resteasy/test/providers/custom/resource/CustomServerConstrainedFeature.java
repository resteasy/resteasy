package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

/**
 * A feature constrained to the server runtime.
 * 
 * @author pjurak
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class CustomServerConstrainedFeature implements Feature {
   private static volatile boolean invoked = false;
   private Logger logger = Logger.getLogger(CustomServerConstrainedFeature.class);

   @Override
   public boolean configure(FeatureContext context)
   {
      logger.info("Configuring CustomServerConstrainedFeature");
      invoked = true;
      return true;
   }

   public static boolean wasInvoked() {
      return invoked;
  }

  public static void reset() {
      invoked = false;
  }
}
