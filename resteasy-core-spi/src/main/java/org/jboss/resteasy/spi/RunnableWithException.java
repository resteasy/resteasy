package org.jboss.resteasy.spi;

@FunctionalInterface
public interface RunnableWithException {
    void run() throws Exception;
}
