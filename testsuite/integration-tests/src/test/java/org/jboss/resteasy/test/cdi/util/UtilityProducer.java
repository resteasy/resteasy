package org.jboss.resteasy.test.cdi.util;

import java.util.Random;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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
