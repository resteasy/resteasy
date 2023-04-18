package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("resource/subresource/sub")
public class ResourceMatchingAnotherSubResource {

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public String sub() {
        return getClass().getSimpleName();
    }

    @POST
    public String subsub() {
        return sub() + sub();
    }

    @GET
    public String get() {
        return sub();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getget() {
        return subsub();
    }

    @GET
    @Produces("text/*")
    public String getTextStar() {
        return "text/*";
    }

    @POST
    @Consumes("text/*")
    public String postTextStar() {
        return "text/*";
    }
}
