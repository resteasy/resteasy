package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;

public class GenericSuperInterfaceAssignedPermissionsResource {
    @GET
    String hello() {
        return "hello";
    }
}
