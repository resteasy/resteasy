package org.jboss.resteasy.test.providers.jaxb.resource;

import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/test")
public class CharSetResource {

    private static Logger logger = Logger.getLogger(CharSetResource.class.getName());

    @POST
    @Consumes("application/xml")
    public Response post(CharSetCustomer cust) {
        logger.info(cust.getName());
        String name = "bill\u00E9";
        boolean equal = false;
        String test = new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        if (test.compareTo(cust.getName()) == 0) {
            equal = true;
        }
        return equal ? Response.ok().build() : Response.serverError().build();
    }

    @POST
    @Path("string")
    public Response postString(String cust) {
        logger.info(cust);
        boolean equal = false;
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><customer><name>billé</name></customer>";
        if (expected.compareTo(cust) == 0) {
            equal = true;
        }
        return equal ? Response.ok().build() : Response.serverError().build();
    }
}
