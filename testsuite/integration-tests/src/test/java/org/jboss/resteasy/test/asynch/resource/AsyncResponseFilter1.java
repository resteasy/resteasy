package org.jboss.resteasy.test.asynch.resource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

@Priority(1)
@Provider
public class AsyncResponseFilter1 extends AsyncResponseFilter {

    public AsyncResponseFilter1() {
        super("ResponseFilter1");
    }
}
