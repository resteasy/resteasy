package org.jboss.resteasy.test.resource.resource;

import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;


@Path("/optional")
public class OptionalResource {
    @Path("/string")
    @GET
    public String string(@QueryParam("value") Optional<String> value) {
        return value.orElse("none");
    }

    @Path("/holder")
    @GET
    public String holder(@QueryParam("value") Optional<Holder<String>> value) {
        return value.map(Holder::get).orElse("none");
    }

    @Path("/long")
    @POST
    public String optLong(@FormParam("value") OptionalLong value) {
        return Long.toString(value.orElse(42));
    }

    @Path("/double")
    @GET
    public String optDouble(@QueryParam("value") OptionalDouble value) {
        return Double.toString(value.orElse(4242.0));
    }

    @Path("/int")
    @GET
    public String optInt(@QueryParam("value") OptionalInt value) {
        return Integer.toString(value.orElse(424242));
    }

    public static class Holder<T> {
        private final T value;
        private Holder(final T value) {
            this.value = value;
        }

        T get() {
            return value;
        }

        public static Holder<String> valueOf(String value) {
            return new Holder<>(value);
        }
    }

    @Path("/matrix")
    @POST
    public String matrix(@MatrixParam("value") OptionalLong value) {
        return Long.toString(value.orElse(42));
    }

    @Path("/path/{value}")
    @GET
    public String path(@PathParam("value") OptionalLong value) { return Long.toString(value.orElse(42)); }

    @Path("/header")
    @GET
    public String header(@HeaderParam("value") OptionalLong value) { return Long.toString(value.orElse(42)); }

    @Path("/cookie")
    @GET
    public String cookie(@CookieParam("value") OptionalLong value) { return Long.toString(value.orElse(42)); }
}
