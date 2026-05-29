package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/record")
public class RecordBeanParamResource {

    /**
     * Test endpoint for Record with @FormParam
     */
    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecordForm(@BeanParam RecordFormParam form) {
        return form.name() + ":" + form.age() + ":" + form.email();
    }

    /**
     * Test endpoint for Record with mixed parameter types
     */
    @POST
    @Path("/mixed")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecordMixed(@BeanParam RecordMixedParam form) {
        return form.name() + ":" + form.age() + ":" + form.country() + ":" + form.userAgent();
    }

    /**
     * Test endpoint for immutable class with constructor injection
     */
    @POST
    @Path("/immutable")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postImmutableClass(@BeanParam ImmutableClassFormParam form) {
        return form.getUsername() + ":" + form.getPassword();
    }

    /**
     * Test endpoint for Record with default values
     */
    @POST
    @Path("/defaults")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecordDefaults(@BeanParam RecordDefaultsParam form) {
        return form.name() + ":" + form.age();
    }

    /**
     * Test endpoint for Record with nullable values
     */
    @POST
    @Path("/nullable")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecordNullable(@BeanParam RecordNullableParam form) {
        return form.name() + ":" + form.email();
    }

    /**
     * Test endpoint for Record with primitive types
     */
    @POST
    @Path("/primitives")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecordPrimitives(@BeanParam RecordPrimitivesParam form) {
        return form.count() + ":" + form.active() + ":" + form.score();
    }

    /**
     * Test endpoint for traditional mutable class with property injection
     */
    @POST
    @Path("/mutable")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String postMutableClass(@BeanParam MutableClassFormParam form) {
        return form.getName() + ":" + form.getAge() + ":" + form.getEmail();
    }
}
