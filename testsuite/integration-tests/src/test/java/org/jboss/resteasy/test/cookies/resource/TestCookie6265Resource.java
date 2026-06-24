/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.cookies.resource;

import java.util.Date;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.NewCookie.SameSite;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.cookies.NewCookie6265;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("")
public class TestCookie6265Resource {

    @GET
    @Path("getNewCookies/6265")
    public Response getNewCookies6265() throws Exception {
        NewCookie6265.Builder builder6265a = new NewCookie6265.Builder("name6265a");
        NewCookie6265 cookie6265a = builder6265a.value("value1")
                .version(17)
                .path("/path1")
                .domain("domain1")
                .comment("comment1")
                .maxAge(23)
                .expiry(new Date(3, 5, 7))
                .secure(false)
                .httpOnly(true)
                .sameSite(SameSite.LAX)
                .extension("a=b")
                .extension("c")
                .build();
        NewCookie6265.Builder builder6265b = new NewCookie6265.Builder("name6265b");
        NewCookie6265 cookie6265b = builder6265b.value("value2")
                .version(19)
                .path("/path2")
                .domain("domain2")
                .comment("comment2")
                .maxAge(29)
                .expiry(new Date(5, 7, 11))
                .secure(true)
                .httpOnly(false)
                .sameSite(SameSite.STRICT)
                .extension("d=e")
                .extension("f")
                .build();
        return Response.ok().cookie(cookie6265a, cookie6265b).build();
    }

    @GET
    @Path("getNewCookies/2109")
    public Response getNewCookies2109() throws Exception {
        NewCookie.Builder builder2109a = new NewCookie.Builder("name2109a");
        NewCookie cookie2109a = builder2109a.value("value1")
                .version(17)
                .path("/path1")
                .domain("domain1")
                .comment("comment1")
                .maxAge(23)
                .expiry(new Date(3, 5, 7))
                .secure(false)
                .httpOnly(true)
                .sameSite(SameSite.LAX)
                .build();
        NewCookie.Builder builder2109b = new NewCookie.Builder("name2109b");
        NewCookie cookie2109b = builder2109b.value("value2")
                .version(19)
                .path("/path2")
                .domain("domain2")
                .comment("comment2")
                .maxAge(29)
                .expiry(new Date(5, 7, 11))
                .secure(true)
                .httpOnly(false)
                .sameSite(SameSite.STRICT)
                .build();
        return Response.ok().cookie(cookie2109a, cookie2109b).build();
    }

    @GET
    @Path("checkCookies/cookies/6265")
    public Response checkCookiesCookies6265(
            @CookieParam("name6265a") Cookie cookie6265a,
            @CookieParam("name6265b") Cookie cookie6265b,
            @CookieParam("Domain") Cookie domain) throws Exception {
        boolean b6265a = "name6265a=value1".equals(cookie6265a.toString());
        boolean b6265b = "name6265b=value2".equals(cookie6265b.toString());
        boolean bDomain = domain == null;
        return Response.ok(b6265a && b6265b && bDomain).build();
    }

    @GET
    @Path("checkCookies/string/6265")
    public Response checkCookiesString6265(
            @CookieParam("name6265a") String cookie6265a,
            @CookieParam("name6265b") String cookie6265b,
            @CookieParam("Domain") String domain) throws Exception {
        boolean b6265a = "value1".equals(cookie6265a);
        boolean b6265b = "value2".equals(cookie6265b);
        boolean bDomain = domain == null;
        return Response.ok(b6265a && b6265b && bDomain).build();
    }

    @GET
    @Path("checkCookies/cookies/2109")
    public Response checkCookiesCookies2109(
            @CookieParam("name2109a") Cookie cookie2109a,
            @CookieParam("name2109b") Cookie cookie2109b,
            @CookieParam("Domain") Cookie domain) throws Exception {
        System.out.println("cookie2109a: " + cookie2109a.toString());
        System.out.println("cookie2109b: " + cookie2109b.toString());
        boolean b2109a = "name2109a".equals(cookie2109a.getName()) &&
                "value1".equals(cookie2109a.getValue()) &&
                "/path1".equals(cookie2109a.getPath()) &&
                "domain1".equals(cookie2109a.getDomain()) &&
                "name2109a=value1; $Domain=domain1; $Path=/path1".equals(cookie2109a.toString());
        boolean b2109b = "name2109b".equals(cookie2109b.getName()) &&
                "value2".equals(cookie2109b.getValue()) &&
                "/path2".equals(cookie2109b.getPath()) &&
                "domain2".equals(cookie2109b.getDomain()) &&
                "name2109b=value2; $Domain=domain2; $Path=/path2".equals(cookie2109b.toString());
        boolean bDomain = domain == null;
        return Response.ok(b2109a && b2109b && bDomain).build();
    }

    @GET
    @Path("checkCookies/string/2109")
    public Response checkCookiesString2109(
            @CookieParam("name2109a") String cookie2109a,
            @CookieParam("name2109b") String cookie2109b,
            @CookieParam("Domain") String domain) throws Exception {
        boolean b2109a = "value1".equals(cookie2109a);
        boolean b2109b = "value2".equals(cookie2109b);
        boolean bDomain = domain == null;
        return Response.ok(b2109a && b2109b && bDomain).build();
    }
}
