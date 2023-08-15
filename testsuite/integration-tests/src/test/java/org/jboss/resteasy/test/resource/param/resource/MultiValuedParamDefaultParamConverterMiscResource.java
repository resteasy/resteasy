package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Separator;
import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.junit.Assert;

@Path("misc")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class MultiValuedParamDefaultParamConverterMiscResource {

    /**
     * Not for proxies due to @Separator regular expression.
     */
    @Path("regex")
    @GET
    public String regex(@QueryParam("w") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
        StringBuffer sb = new StringBuffer();
        for (MultiValuedParamDefaultParamConverterConstructorClass s : list) {
            sb.append(s.getS()).append("|");
        }
        return sb.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // For the following tests, MultiValuedParamDefaultParamConverterMiscResourceIntf uses
    // the invalid @Separator("(-)"), so that MultiValuedParamConverterProvider is prevented
    // from returning a ParamConverter on the client side. Consequently, multiple elements
    // will be sent according to the default syntax, and the use of @Separator("-") will not
    // apply, so that parameters will be parsed as single elements.

    @Inject
    UriInfo info;
    @Inject
    HttpHeaders headers;

    @Path("regex/client/cookie")
    @GET
    public String regexClientCookie(@Separator("-") @CookieParam("p") Set<String> ss) {
        Assert.assertEquals(1, ss.size());
        return headers.getHeaderString("Cookie");
    }

    @Path("regex/client/header")
    @GET
    public String regexClientHeader(@Separator("-") @HeaderParam("p") Set<String> ss) {
        Assert.assertEquals(1, ss.size());
        return headers.getHeaderString("p");
    }

    @Path("regex/client/matrix")
    @GET
    public String regexClientMatrix(@Separator("-") @MatrixParam("p") Set<String> ss) {
        Assert.assertEquals(1, ss.size());
        return info.getRequestUri().toString();
    }

    @Path("regex/client/path/{p}")
    @GET
    public String regexClientPath(@Separator("-") @PathParam("p") Set<String> ss) {
        Assert.assertEquals(1, ss.size());
        return info.getRequestUri().toString();
    }

    @Path("regex/client/query")
    @GET
    public String regexClientQuery(@Separator("-") @QueryParam("p") Set<String> ss) {
        Assert.assertEquals(1, ss.size());
        return info.getRequestUri().toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // In the following tests, an invalid @Separator prevents MultiValuedParamConverterProvider from returning a
    // ParamConverter on the server side. Therefore, parameters of the form "p1-p2" will be treated as a single element.

    @Path("regex/server/cookie")
    @GET
    public String regexServerCookie(@Separator("(-)") @CookieParam("p") Set<String> ss) {
        return concat(ss);
    }

    @Path("regex/server/header")
    @GET
    public String regexServerHeader(@Separator("(-)") @HeaderParam("p") Set<String> ss) {
        return concat(ss);
    }

    @Path("regex/server/matrix")
    @GET
    public String regexServerMatrix(@Separator("(-)") @MatrixParam("p") Set<String> ss) {
        return concat(ss);
    }

    @Path("regex/server/path/{p}")
    @GET
    public String regexServerPath(@Separator("(-)") @PathParam("p") Set<String> ss) {
        return concat(ss);
    }

    @Path("regex/server/query")
    @GET
    public String regexServerQuery(@Separator("(-)") @QueryParam("p") Set<String> ss) {
        return concat(ss);
    }

    String concat(Set<String> ss) {
        StringBuffer sb = new StringBuffer();
        for (String s : ss) {
            sb.append(s);
            sb.append("|");
        }
        return sb.toString();
    }
}
