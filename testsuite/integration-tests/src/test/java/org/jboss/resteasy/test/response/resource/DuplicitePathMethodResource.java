package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/g")
public class DuplicitePathMethodResource {
    public static final String DUPLICITE_RESPONSE_1 = "response1";
    public static final String DUPLICITE_RESPONSE_2 = "response2";
    public static final String NO_DUPLICITE_RESPONSE = "response3";
    public static final String DUPLICITE_TYPE_GET = "response7";
    public static final String DUPLICITE_TYPE_POST = "response8";

    @Path("/h")
    @GET
    public String dupliciteOne() {
        return DUPLICITE_RESPONSE_1;
    }

    @Path("/h")
    @GET
    public String dupliciteTwo() {
        return DUPLICITE_RESPONSE_2;
    }

    @Path("/i")
    @GET
    public String noDuplicite() {
        return NO_DUPLICITE_RESPONSE;
    }

    @Path("/j")
    @POST
    public String dupliciteDifferentTypePost() {
        return DUPLICITE_TYPE_POST;
    }

    @Path("/j")
    @GET
    public String dupliciteDifferentTypeGet() {
        return DUPLICITE_TYPE_GET;
    }
}
