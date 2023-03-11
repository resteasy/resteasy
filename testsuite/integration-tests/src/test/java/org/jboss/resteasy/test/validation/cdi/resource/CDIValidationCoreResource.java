package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@CDIValidationCoreSumConstraint(min = 9)
public class CDIValidationCoreResource {
    @Min(3)
    @PathParam("field")
    protected int field;

    private int property;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Min(11)
    @Path("input/{field}/{property}/{param}")
    public int inputs(@Min(7) @PathParam("param") int param) {
        return param;
    }

    @Min(5)
    public int getProperty() {
        return property;
    }

    @PathParam("property")
    public void setProperty(int property) {
        this.property = property;
    }

    @POST
    @Path("setter/{field}/{property}/{param}")
    public void setTest(@Min(7) int param) {
    }

    @Path("locator/{field}/{property}/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public Object locator(@Min(11) @PathParam("param") int param) {
        return new CDIValidationCoreSubResource();
    }

    @GET
    @Path("none/{field}/{property}/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    @ValidateOnExecution(type = { ExecutableType.NONE })
    public Object none(@Min(11) @PathParam("param") int param) {
        return param;
    }

    @GET
    @Path("noParams/{field}/{property}")
    @Produces(MediaType.TEXT_PLAIN)
    public Object noParams() {
        return "noParams";
    }
}
