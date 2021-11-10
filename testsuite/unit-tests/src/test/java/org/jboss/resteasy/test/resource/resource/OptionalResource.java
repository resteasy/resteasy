package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;


@Path("/optional")
public class OptionalResource {

    @QueryParam("valueQ1")
    Optional<String> queryParam1;
    @QueryParam("valueQ2")
    Optional<Holder<String>> queryParam2;
    @FormParam("valueF")
    OptionalLong formParam;
    @QueryParam("valueQ3")
    OptionalDouble queryParam3;
    @QueryParam("valueQ4")
    OptionalInt queryParam4;
    @MatrixParam("valueM")
    OptionalLong matrixParam;
    @HeaderParam("valueH")
    OptionalLong headerParam;
    @CookieParam("valueC")
    OptionalLong cookieParam;


    @Path("/string")
    @GET
    public String string(@QueryParam("valueQ1") Optional<String> value, @BeanParam Bean bean) {
        if (!value.equals(queryParam1) || !value.equals(bean.queryParam1)) {
            throw new IllegalStateException("Values are not equal");
        }
        return value.orElse("none");
    }

    @Path("/holder")
    @GET
    public String holder(@QueryParam("valueQ2") Optional<Holder<String>> value, @BeanParam Bean bean) {
        if (!value.equals(queryParam2) || !value.equals(bean.queryParam2)) {
            throw new IllegalStateException("Values are not equal");
        }
        return value.map(Holder::get).orElse("none");
    }

    @Path("/long")
    @POST
    public String optLong(@FormParam("valueF") OptionalLong value, @BeanParam Bean bean) {
        if (!value.equals(formParam) || !value.equals(bean.formParam)) {
            throw new IllegalStateException("Values are not equal");
        }
        return Long.toString(value.orElse(42));
    }

    @Path("/double")
    @GET
    public String optDouble(@QueryParam("valueQ3") OptionalDouble value, @BeanParam Bean bean) {
        if (!value.equals(queryParam3) || !value.equals(bean.queryParam3)) {
            throw new IllegalStateException("Values are not equal");
        }
        return Double.toString(value.orElse(4242.0));
    }

    @Path("/int")
    @GET
    public String optInt(@QueryParam("valueQ4") OptionalInt value, @BeanParam Bean bean) {
        if (!value.equals(queryParam4) || !value.equals(bean.queryParam4)) {
            throw new IllegalStateException("Values are not equal");
        }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Holder<?> holder = (Holder<?>) o;
            return Objects.equals(value, holder.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    @Path("/matrix")
    @POST
    public String matrix(@MatrixParam("valueM") OptionalLong value, @BeanParam Bean bean) {
        if (!value.equals(matrixParam) || !value.equals(bean.matrixParam)) {
            throw new IllegalStateException("Values are not equal");
        }
        return Long.toString(value.orElse(42));
    }

    @Path("/header")
    @GET
    public String header(@HeaderParam("valueH") OptionalLong value, @BeanParam Bean bean) {
        if (!value.equals(headerParam) || !value.equals(bean.headerParam)) {
            throw new IllegalStateException("Values are not equal");
        }
        return Long.toString(value.orElse(42));
    }

    @Path("/cookie")
    @GET
    public String cookie(@CookieParam("valueC") OptionalLong value, @BeanParam Bean bean) {
        if (!value.equals(cookieParam) || !value.equals(bean.cookieParam)) {
            throw new IllegalStateException("Values are not equal");
        }
        return Long.toString(value.orElse(42));
    }

    public static class Bean {
        @QueryParam("valueQ1")
        Optional<String> queryParam1;
        @QueryParam("valueQ2")
        Optional<Holder<String>> queryParam2;
        @FormParam("valueF")
        OptionalLong formParam;
        @QueryParam("valueQ3")
        OptionalDouble queryParam3;
        @QueryParam("valueQ4")
        OptionalInt queryParam4;
        @MatrixParam("valueM")
        OptionalLong matrixParam;
        @HeaderParam("valueH")
        OptionalLong headerParam;
        @CookieParam("valueC")
        OptionalLong cookieParam;
    }
}
