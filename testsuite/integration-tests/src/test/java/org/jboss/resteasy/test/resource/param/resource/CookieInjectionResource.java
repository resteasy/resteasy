package org.jboss.resteasy.test.resource.param.resource;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Cookie;


@Path("/")
public class CookieInjectionResource {
    @Path("/set")
    @GET
    public Response set() {
        return Response.ok("content").cookie(new NewCookie("meaning", "42")).build();
    }
    
    @Path("/expire")
    @GET
    public Response expire() {
        NewCookie cookie = new NewCookie("Name", "Value", "/", "*", 0, "comment", 3600, new Date(),
                true, true);
        return Response.ok().cookie(cookie).entity(cookie.toString()).build();
    }

    @Path("/expire1")
    @GET
    public Response expire1() {
        NewCookie cookie = new NewCookie("Name", "Value", "/", "*", 1, "comment", 3600, new Date(),
                true, true);
        return Response.ok().cookie(cookie).entity(cookie.toString()).build();
    }

    @Path("/expired")
    @GET
    public Response expired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        NewCookie cookie = new NewCookie("Name", "Value", "/", "*", 1, "comment", 1800, cal.getTime(),
                true, true);
        return Response.ok().cookie(cookie).entity(cookie.toString()).build();
    }

    @Context
    HttpHeaders myHeaders;

    @Path("/headers")
    @GET
    public String headers(@Context HttpHeaders headers) {
        String value = headers.getCookies().get("meaning").getValue();
        Assert.assertEquals("Unexpected value in the cookie", value, "42");
        return value;
    }

    @Path("/headers/fromField")
    @GET
    public String headersFromField(@Context HttpHeaders headers) {
        String value = myHeaders.getCookies().get("meaning").getValue();
        Assert.assertEquals("Unexpected value in the cookie", value, "42");
        return value;
    }

    @Path("/param")
    @GET
    @Produces("text/plain")
    public int param(@CookieParam("meaning") int value) {
        Assert.assertEquals("Unexpected value in the cookie", value, 42);
        return value;
    }

    @Path("/cookieparam")
    @GET
    public String param(@CookieParam("meaning") Cookie value) {
        Assert.assertEquals("Unexpected value in the cookie", value.getValue(), "42");
        return value.getValue();
    }

    @Path("/default")
    @GET
    @Produces("text/plain")
    public int defaultValue(@CookieParam("defaulted") @DefaultValue("24") int value) {
        Assert.assertEquals("Unexpected value in the cookie", value, 24);
        return value;
    }
}
