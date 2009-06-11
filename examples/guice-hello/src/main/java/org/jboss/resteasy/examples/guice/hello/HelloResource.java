package org.jboss.resteasy.examples.guice.hello;

import com.google.inject.Inject;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;

@Path("hello")
public class HelloResource
{
   private final Greeter greeter;

   @Inject
   public HelloResource(final Greeter greeter)
   {
      this.greeter = greeter;
   }

   @GET
   @Path("{name}")
   public String hello(@PathParam("name") final String name) {
      return greeter.greet(name);
   }
}
