package org.jboss.resteasy.test.cdi.util;

import java.util.Random;
import java.util.logging.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@Dependent
public class UtilityProducer {
    private static Random random = new Random();

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Produces
    public int randomInt() {
        return random.nextInt();
    }
}
