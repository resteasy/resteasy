package org.jboss.resteasy.test.form.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/test")
public class FormParamPutResource {

    private static Logger logger = Logger.getLogger(FormParamPutResource.class);
    private static volatile String formParam;

    @PUT
    @Path("/{pathParam:\\d+}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"application/xml", "application/json"})
    public void updateGuestPostStatus(@PathParam("pathParam") Long pathParam,
                                      @FormParam("formParam") String formParam) {

        this.formParam = formParam;
        logger.info("===============");
        logger.info(formParam);
        logger.info("===============");
    }

    @GET
    public String getStatus() {
        return formParam;
    }

}
