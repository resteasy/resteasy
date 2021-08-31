package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;

public class GenericSuperInterfaceAssignedPermissionsResource {
   @GET
   String hello() {
      return "hello";
   }
}
