package org.jboss.resteasy.jsapi.testing;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author Weinan Li
 * @created_at 08 24 2012
 */
@Path("/smoke")
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

    @POST
    @Path("/RESTEASY-731/false")
    public String testRESTEasy731False(@FormParam("false") boolean bool) {
        return ("RESTEASY-731-" + String.valueOf(bool));
    }

    @POST
    @Path("/RESTEASY-731/zero")
    public String testRESTEasy731Zero(@FormParam("zero") int zero) {
        return ("RESTEASY-731-" + String.valueOf(zero));
    }
}
