package org.jboss.resteasy.test.interceptor.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class GZIPAnnotationResource implements GZIPAnnotationInterface {
    @Inject
    HttpHeaders headers;

    @Path("/foo")
    @Consumes("text/plain")
    @Produces("text/plain")
    @GZIP
    @POST
    @Override
    public String getFoo(String request) {

        if ("test".equals(request)) {
            String contentEncoding = headers.getRequestHeader(HttpHeaders.CONTENT_ENCODING).get(0);
            String acceptEncoding = headers.getRequestHeader(HttpHeaders.ACCEPT_ENCODING).get(0);
            return contentEncoding + "|" + acceptEncoding;
        } else {
            throw new RuntimeException("request != \"test\"");
        }
    }
}
