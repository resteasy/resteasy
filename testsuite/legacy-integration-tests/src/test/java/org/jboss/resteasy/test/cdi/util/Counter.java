package org.jboss.resteasy.test.cdi.util;

import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

@Singleton
@CounterBinding
@ApplicationScoped
public class Counter {
    private static Logger logger = Logger.getLogger(Counter.class);

    public static final int INITIAL_VALUE = 17;
    private static AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);

    public int getNext() {
        logger.info("In Counter: counter: " + counter);
        return counter.getAndIncrement();
    }

    public void reset() {
        counter.set(INITIAL_VALUE);
    }
}

