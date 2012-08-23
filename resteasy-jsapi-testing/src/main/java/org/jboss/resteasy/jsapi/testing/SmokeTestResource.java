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
    public String testFormParam(@FormParam("key") String[] values) {
        String val = "";
        for (String _val : values) {
            val += _val + "::";
        }
        return val;
    }

    @Path("/post2")
    @POST
    public String testFormParam2(@FormParam("key") String val) {
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
    public String testCookieParam(@CookieParam("username") String key) {
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

    @GET
    @Path("/header")
    public String testHeaderParam(@HeaderParam("Referer") String referer) {
        return referer;
    }
}
