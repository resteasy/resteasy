package org.jboss.resteasy.test.resource.param.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.jboss.resteasy.test.resource.param.UserDefinedHeaderParamTest;

@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class UserDefinedHeaderParamResource implements UserDefinedHeaderParamTest.UserHeaderParamInterface {

    @Inject
    private HttpHeaders httpHeaders;

    @GET
    @Path("/header-first")
    public String sendHeaderFirst(@HeaderParam("Content-Type") String contentType, String text) {
        return httpHeaders.getHeaderString("Content-Type");
    }

    @GET
    @Path("/text-first")
    public String sendTextFirst(String text, @HeaderParam("Content-Type") String contentType) {
        return httpHeaders.getHeaderString("Content-Type");
    }

    @GET
    @Path("/header")
    public String sendDefaultType(String text) {
        return httpHeaders.getHeaderString("Content-Type");
    }

    @GET
    @Path("/header")
    public String sendMultipleTypes(String text, @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Content-Type") String secondContentType,
            @HeaderParam("Content-Type") String thirdContentType) {
        return httpHeaders.getHeaderString("Content-Type");
    }
}
