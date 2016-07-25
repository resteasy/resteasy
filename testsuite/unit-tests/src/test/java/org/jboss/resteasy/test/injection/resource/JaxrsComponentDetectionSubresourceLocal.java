package org.jboss.resteasy.test.injection.resource;

import javax.ws.rs.GET;

public interface JaxrsComponentDetectionSubresourceLocal {
    @GET
    void foo();
}
