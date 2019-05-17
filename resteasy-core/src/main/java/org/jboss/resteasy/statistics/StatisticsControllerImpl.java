package org.jboss.resteasy.statistics;

import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.statistics.MethodStatisticsLogger;
import org.jboss.resteasy.spi.statistics.StatisticsController;

import java.util.ArrayList;
import java.util.List;

public class StatisticsControllerImpl implements StatisticsController {

   // When statistics collection is OFF use the no-op's methods.
   public static final MethodStatisticsLogger EMPTY = new MethodStatisticsLogger() {
      // use all no-op methods
   };

   boolean isEnabled = false;
   List<ResourceInvoker> registry = new ArrayList<>();

   @Override
   public void register(ResourceInvoker invoker) {
      if(isEnabled) {
         invoker.setMethodStatisticsLogger(new MethodStatisticsLoggerImpl());
      }
      registry.add(invoker);
   }

   @Override
   public void setEnabled(boolean b) {
      if (isEnabled == b)
      {
         return;
      }

      isEnabled = b;
      if (isEnabled) {
         for (ResourceInvoker invoker : registry) {
            invoker.setMethodStatisticsLogger(new MethodStatisticsLoggerImpl());
         }
      } else {
         for (ResourceInvoker invoker : registry) {
            invoker.setMethodStatisticsLogger(EMPTY);
         }
      }
   }

   @Override
   public void reset() {
      for (ResourceInvoker invoker : registry) {
         invoker.getMethodStatisticsLogger().reset();
      }
   }
}
