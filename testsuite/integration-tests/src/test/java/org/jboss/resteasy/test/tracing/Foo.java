package org.jboss.resteasy.test.tracing;

import jakarta.ws.rs.GET;

public class Foo {
   @GET
   public String get() {
      return "{|FOO|}";
   }
}
