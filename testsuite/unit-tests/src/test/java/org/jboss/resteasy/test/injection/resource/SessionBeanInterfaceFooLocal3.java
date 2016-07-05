package org.jboss.resteasy.test.injection.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("foo")
public interface SessionBeanInterfaceFooLocal3 {
    @GET
    String foo3();
}
