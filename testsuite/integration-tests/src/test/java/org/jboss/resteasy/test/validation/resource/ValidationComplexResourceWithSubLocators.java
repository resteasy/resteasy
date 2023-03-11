package org.jboss.resteasy.test.validation.resource;

import java.lang.reflect.Field;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;

@Path("/")
public class ValidationComplexResourceWithSubLocators {
    @Path("validField")
    public ValidationComplexResourceWithValidField validField() {
        return new ValidationComplexResourceWithValidField();
    }

    @Path("invalidField")
    public ValidationComplexResourceWithInvalidField invalidField() {
        return new ValidationComplexResourceWithInvalidField();
    }

    @Path("property/{s}")
    public ValidationComplexResourceWithProperty property(@PathParam("s") String s) {
        ValidationComplexResourceWithProperty subResource = new ValidationComplexResourceWithProperty();
        subResource.setS(s);
        return subResource;
    }

    @Path("locator")
    public SubResource sub() {
        return new SubResource();
    }

    @Path("everything/{s}/{t}")
    public ValidationComplexResourceWithAllFivePotentialViolations everything(@PathParam("s") String s,
            @PathParam("t") String t) {
        ValidationComplexResourceWithAllFivePotentialViolations subresource = new ValidationComplexResourceWithAllFivePotentialViolations();
        try {
            Field field = ValidationComplexResourceWithAllFivePotentialViolations.class.getDeclaredField("s");
            field.setAccessible(true);
            field.set(subresource, s);
            subresource.setT(t);
            return subresource;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("")
    public static class SubResource {
        @Path("sublocator/{s}")
        public SubSubResource sub(@PathParam("s") String s) {
            SubSubResource ssr = new SubSubResource();
            ssr.setS(s);
            return ssr;
        }
    }

    @Path("")
    public static class SubSubResource {
        @Size(min = 2, max = 3)
        String s;

        public SubSubResource() {
        }

        public void setS(String s) {
            this.s = s;
        }

        @POST
        public void subSub() {
        }
    }
}
