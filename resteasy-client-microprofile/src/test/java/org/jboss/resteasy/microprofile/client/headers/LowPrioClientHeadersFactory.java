package org.jboss.resteasy.microprofile.client.headers;


import org.jboss.resteasy.microprofile.client.header.HeaderFiller;
import org.jboss.resteasy.microprofile.client.header.HeaderFillerFactory;

import java.util.Arrays;

public class LowPrioClientHeadersFactory implements HeaderFillerFactory {
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public HeaderFiller createFiller(String value, String headerName, boolean required, Class<?> interfaceClass, Object clientProxy) {
        return () -> Arrays.asList("low", "prio");
    }
}
