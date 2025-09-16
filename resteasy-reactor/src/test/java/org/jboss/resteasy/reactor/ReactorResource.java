package org.jboss.resteasy.reactor;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Path("/")
public class ReactorResource {
    static final AtomicInteger monoEndpointCounter = new AtomicInteger(0);

    @Path("mono")
    @GET
    public Mono<String> mono() {
        monoEndpointCounter.incrementAndGet();
        return Mono.just("got it");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("flux")
    @GET
    @Stream
    public Flux<String> flux() {
        return Flux.just("one", "two");
    }

    @Path("injection")
    @GET
    public Mono<Integer> injection(@Context Integer value) {
        return Mono.just(value);
    }

    @Path("injection-async")
    @GET
    public Mono<Integer> injectionAsync(@Async @Context Integer value) {
        return Mono.just(value);
    }
}
