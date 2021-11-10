package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ws.rs.GET;

public interface SingletonLocalIF {
   @GET
   String get();
}
