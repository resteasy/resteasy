package org.jboss.resteasy.microprofile.client.headers;


import org.jboss.resteasy.microprofile.client.header.HeaderFiller;
import org.jboss.resteasy.microprofile.client.header.HeaderFillerFactory;

import java.util.Arrays;

public class HighPrioClientHeadersFactory implements HeaderFillerFactory {
    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public HeaderFiller createFiller(String value, String headerName, boolean required, Class<?> interfaceClass, Object clientProxy) {
        return () -> Arrays.asList("high", "prio");
    }
}
