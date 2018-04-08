package org.jboss.resteasy.test.providers.jackson2.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/")
public class JsonFormResource {

    public JsonFormResource() {
    }

    public static class Form {
        @FormParam("user")
        @PartType("application/json")
        private JsonUser user;

        public Form() {
        }

        public Form(final JsonUser user) {
            this.user = user;
        }

        public JsonUser getUser() {
            return user;
        }
    }

    @PUT
    @Path("form/class")
    @Consumes("multipart/form-data")
    public String putMultipartForm(@MultipartForm Form form) {
        return form.getUser().getName();
    }
}