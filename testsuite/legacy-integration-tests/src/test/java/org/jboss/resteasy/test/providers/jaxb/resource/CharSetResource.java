package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

@Path("/test")
public class CharSetResource {

    private static Logger logger = Logger.getLogger(CharSetResource.class.getName());

    @POST
    @Consumes("application/xml")
    public Response post(CharSetCustomer cust) {
        logger.info(cust.getName());
        String name = "bill\u00E9";
        boolean equal = false;
        try {
            String test = new String(name.getBytes("UTF-8"), "UTF-8");
            if (test.compareTo(cust.getName()) == 0) {
                equal = true;
            }
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Not supported encoding.");
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
