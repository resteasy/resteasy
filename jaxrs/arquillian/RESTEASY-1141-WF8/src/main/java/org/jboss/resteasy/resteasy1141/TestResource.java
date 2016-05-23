package org.jboss.resteasy.resteasy1141;

import javax.ws.rs.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/test")
public class TestResource {

    private volatile static String formParam;

    @PUT
    @Path("/{pathParam:\\d+}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"application/xml", "application/json"})
    public void updateGuestPostStatus(@PathParam("pathParam") Long pathParam,
                                      @FormParam("formParam") String formParam) {

        this.formParam = formParam;
        System.out.println("===============");
        System.out.println(formParam);
        System.out.println("===============");
    }

    @GET
    public String getStatus() {
        return formParam;
    }

}
