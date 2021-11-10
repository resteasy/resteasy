package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.PUT;

public interface ParameterSubResInternalInterface<T extends Number> {
   @PUT
   void foo(T value);
}
