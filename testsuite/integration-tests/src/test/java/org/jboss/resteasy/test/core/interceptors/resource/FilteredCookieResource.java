package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("test")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class FilteredCookieResource {
    private static final String OLD_COOKIE_NAME = "old-cookie";
    private static final String NEW_COOKIE_NAME = "new-cookie";
    private @Inject HttpHeaders headers;

    @GET
    @Path("get")
    public Response getCookie() {
        NewCookie cookie = new NewCookie.Builder(OLD_COOKIE_NAME)
                .value("value")
                .build();
        return Response.ok().cookie(cookie).build();
    }

    @GET
    @Path("return")
    public Response returnCookie() {
        Cookie oldCookie = headers.getCookies().get(OLD_COOKIE_NAME);
        Cookie newCookie = headers.getCookies().get(NEW_COOKIE_NAME);
        ResponseBuilder builder = Response.ok();
        NewCookie nck1 = new NewCookie.Builder(oldCookie.getName())
                .value(oldCookie.getValue())
                .build();
        NewCookie nck2 = new NewCookie.Builder(newCookie.getName())
                .value(newCookie.getValue())
                .build();
        builder.cookie(nck1);
        builder.cookie(nck2);
        return builder.build();
    }
}
