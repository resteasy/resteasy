package org.jboss.resteasy.statistics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.statistics.MethodStatisticsLogger;
import org.jboss.resteasy.spi.statistics.StatisticsController;

public class StatisticsControllerImpl implements StatisticsController {

    // When statistics collection is OFF use the no-op's methods.
    public static final MethodStatisticsLogger EMPTY = new MethodStatisticsLogger() {
        // use all no-op methods
    };

    boolean isEnabled = false;
    List<ResourceInvoker> registry = new CopyOnWriteArrayList<>();

    @Override
    public void register(ResourceInvoker invoker) {
        if (isEnabled) {
            invoker.setMethodStatisticsLogger(new MethodStatisticsLoggerImpl());
        }
        registry.add(invoker);
    }

    @Override
    public void setEnabled(boolean b) {
        if (isEnabled == b) {
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
