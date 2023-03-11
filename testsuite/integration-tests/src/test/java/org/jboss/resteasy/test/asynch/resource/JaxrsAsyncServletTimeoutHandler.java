package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.TimeoutHandler;

public class JaxrsAsyncServletTimeoutHandler implements TimeoutHandler {

    private int function = 0;

    public JaxrsAsyncServletTimeoutHandler(final int function) {
        super();
        this.function = function;
    }

    @Override
    public void handleTimeout(AsyncResponse asyncResponse) {
        switch (function) {
            case 1:
                asyncResponse.setTimeout(200, TimeUnit.MILLISECONDS);
                break;
            case 2:
                asyncResponse.cancel();
                break;
            case 3:
                asyncResponse.resume(JaxrsAsyncServletResource.RESUMED);
                break;
        }
    }

}
