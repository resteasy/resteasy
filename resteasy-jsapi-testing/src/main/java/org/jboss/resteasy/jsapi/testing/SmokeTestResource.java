package org.jboss.resteasy.jsapi.testing;

import javax.ws.rs.*;

/**
 * @author Weinan Li
 * @created_at 08 24 2012
 */
@Path("/")
public class SmokeTestResource {
    @Path("{id}")
    @GET
    @Produces("text/plain")
    public String testPathParam(@PathParam("id") String id) {
        return id;
    }

    @POST
    public String testFormParm(@FormParam("key") String[] values) {
        String val = "";
        for (String _val : values) {
            val += _val + "::";
        }
        return val;
    }

    @GET
    public String testQueryParam(@QueryParam("key") String[] values) {
        String val = "";
        for (String _val : values) {
            val += _val + "::";
        }
        return val;
    }

    @Path("/cookie")
    @GET
    public String testCookieParam(@CookieParam("test-cookie") String key) {
        return key;
    }

    @GET
    @Path("/matrix")
    public String testMatrixParam(@MatrixParam("key") String[] key) {
        String val = "";
        for (String _val : key) {
            val += _val + "::";
        }
        return val;
    }
}
