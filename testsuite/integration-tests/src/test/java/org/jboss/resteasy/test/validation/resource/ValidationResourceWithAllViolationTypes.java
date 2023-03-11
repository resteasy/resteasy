package org.jboss.resteasy.test.validation.resource;

import java.util.logging.Logger;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("all")
@ValidationClassConstraint(5)
public class ValidationResourceWithAllViolationTypes {

    private static Logger logger = Logger.getLogger(ValidationResourceWithAllViolationTypes.class.getName());

    @Size(min = 2, max = 4)
    @PathParam("s")
    String s;

    private String t;

    @Size(min = 3, max = 5)
    public String getT() {
        return t;
    }

    public String retrieveS() {
        return s;
    }

    @PathParam("t")
    public void setT(String t) {
        logger.info(this + " t: " + t);
        this.t = t;
    }

    @POST
    @Path("{s}/{t}")
    @ValidationFooConstraint(min = 4, max = 5)
    public ValidationFoo post(@ValidationFooConstraint(min = 3, max = 5) ValidationFoo validationFoo,
            @PathParam("s") String s) {
        logger.info(this + " s: " + s);
        logger.info(this + " this.s: " + this.s);
        return validationFoo;
    }
}
