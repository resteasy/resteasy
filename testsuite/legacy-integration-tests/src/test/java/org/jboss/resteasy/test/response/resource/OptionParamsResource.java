package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("params")
public class OptionParamsResource {
    @Path("/customers/{custid}/phonenumbers")
    @GET
    @Produces("text/plain")
    public String getPhoneNumbers() {
        return "912-111-1111";
    }

    @Path("/customers/{custid}/phonenumbers/{id}")
    @GET
    @Produces("text/plain")
    public String getPhoneIds() {
        return "1111";
    }
}
