package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

@Provider
@ServerInterceptor
public class AsyncPostProcessingInterceptor implements PostProcessInterceptor {
    public static volatile boolean called;

    @Override
    public void postProcess(ServerResponse response) {
        called = true;
    }
}
