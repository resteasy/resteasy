package org.jboss.resteasy.reactor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Path("/")
public class ReactorResource
{
   @Path("mono")
   @GET
   public Mono<String> mono()
   {
      return Mono.just("got it");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("flux")
   @GET
   @Stream
   public Flux<String> flux()
   {
      return Flux.just("one", "two");
   }

   @Path("injection")
   @GET
   public Mono<Integer> injection(@Context Integer value)
   {
      return Mono.just(value);
   }

   @Path("injection-async")
   @GET
   public Mono<Integer> injectionAsync(@Async @Context Integer value)
   {
      return Mono.just(value);
   }
}
