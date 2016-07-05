package org.jboss.resteasy.test.resource.request.resource;

import org.jboss.resteasy.util.DateUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.Date;

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
