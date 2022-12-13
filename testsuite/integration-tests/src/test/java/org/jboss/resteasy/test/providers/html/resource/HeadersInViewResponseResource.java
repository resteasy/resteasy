package org.jboss.resteasy.test.providers.html.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.html.View;

@Path("/test")
public class HeadersInViewResponseResource {

    @GET
    @Path("get")
    public Response get() {
        NewCookie cookie = new NewCookie.Builder("name1")
                .value("value1")
                .build();
        return Response.ok(new View("/test/view"))
                .header("abc", "123")
                .cookie(cookie)
                .build();
    }

    @GET
    @Path("view")
    public Response view() {
        NewCookie cookie = new NewCookie.Builder("name2")
                .value("value2")
                .build();
        return Response.ok()
                .header("xyz", "789")
                .cookie(cookie)
                .build();
    }
}
