package org.jboss.resteasy.test.resource.param.resource;

import java.util.Calendar;
import java.util.Date;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.utils.CookieUtil;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class CookieInjectionResource {
    @Path("/set")
    @GET
    public Response set() {
        NewCookie cookie = new NewCookie.Builder("meaning")
                .value("42")
                .build();
        return Response.ok("content").cookie(cookie).build();
    }

    @Path("/expire")
    @GET
    public Response expire() {
        NewCookie cookie = new NewCookie.Builder("Name")
                .value("Value")
                .path("/")
                .domain("*")
                .version(0)
                .comment("comment")
                .maxAge(3600)
                .expiry(new Date())
                .secure(true)
                .httpOnly(true)
                .build();
        return Response.ok().cookie(cookie)
                .entity(CookieUtil.toString(NewCookie.class, cookie))
                .build();
    }

    @Path("/expire1")
    @GET
    public Response expire1() {
        NewCookie cookie = new NewCookie.Builder("Name")
                .value("Value")
                .path("/")
                .domain("*")
                .version(1)
                .comment("comment")
                .maxAge(3600)
                .expiry(new Date())
                .secure(true)
                .httpOnly(true)
                .build();
        return Response.ok().cookie(cookie)
                .entity(CookieUtil.toString(NewCookie.class, cookie))
                .build();
    }

    @Path("/expired")
    @GET
    public Response expired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        NewCookie cookie = new NewCookie.Builder("Name")
                .value("Value")
                .path("/")
                .domain("*")
                .version(1)
                .comment("comment")
                .maxAge(1800)
                .expiry(cal.getTime())
                .secure(true)
                .httpOnly(true)
                .build();
        return Response.ok().cookie(cookie)
                .entity(CookieUtil.toString(NewCookie.class, cookie))
                .build();
    }

    @Context
    HttpHeaders myHeaders;

    @Path("/headers")
    @GET
    public String headers(@Context HttpHeaders headers) {
        String value = headers.getCookies().get("meaning").getValue();
        Assertions.assertEquals(value, "42", "Unexpected value in the cookie");
        return value;
    }

    @Path("/headers/fromField")
    @GET
    public String headersFromField(@Context HttpHeaders headers) {
        String value = myHeaders.getCookies().get("meaning").getValue();
        Assertions.assertEquals(value, "42", "Unexpected value in the cookie");
        return value;
    }

    @Path("/param")
    @GET
    @Produces("text/plain")
    public int param(@CookieParam("meaning") int value) {
        Assertions.assertEquals(value, 42, "Unexpected value in the cookie");
        return value;
    }

    @Path("/cookieparam")
    @GET
    public String param(@CookieParam("meaning") Cookie value) {
        Assertions.assertEquals(value.getValue(), "42", "Unexpected value in the cookie");
        return value.getValue();
    }

    @Path("/default")
    @GET
    @Produces("text/plain")
    public int defaultValue(@CookieParam("defaulted") @DefaultValue("24") int value) {
        Assertions.assertEquals(value, 24, "Unexpected value in the cookie");
        return value;
    }
}
