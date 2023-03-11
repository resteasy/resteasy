package org.jboss.resteasy.test.resource.request.resource;

import java.util.Date;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.util.DateUtil;

@Path("/precedence")
public class PreconditionRfc7232PrecedenceResource {
    @GET
    public Response doGet(@Context Request request) {
        Date lastModified = DateUtil.parseDate("Mon, 1 Jan 2007 00:00:00 GMT");
        Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified, new EntityTag("1"));
        if (rb != null) {
            return rb.build();
        }
        return Response.ok("foo", "text/plain").build();
    }
}
