package org.jboss.resteasy.test.core.interceptors.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("resource")
public class ReaderContextResource {

    public static final String HEADERNAME = "FILTER_HEADER";

    public static final String getName() {
        // make this long enough to let entity provider getSize()
        // be enough to let our interceptor name fit in
        return "<resource>" + ReaderContextResource.class.getName() + "</resource>";
    }

    @POST
    @Path("postlist")
    public String postList(List<String> list) {
        return list.iterator().next();
    }

    @GET
    @Path("getlist")
    public Response getList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(getName());
        GenericEntity<ArrayList<String>> entity = new GenericEntity<ArrayList<String>>(
                list) {
        };
        return buildResponse(entity);
    }

    @POST
    @Path("poststring")
    public Response postString(String string) {
        return buildResponse(string);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Send header that would have the power to enable filter / interceptor
    // The header is passed from client request
    @Context
    private HttpHeaders headers;

    private Response buildResponse(Object content) {
        return buildResponse(content, MediaType.WILDCARD_TYPE);
    }

    private Response buildResponse(Object content, MediaType type) {
        List<String> list = headers.getRequestHeader(HEADERNAME);
        String name = null;
        if (list != null && list.size() != 0) {
            name = list.iterator().next();
        }
        Response.ResponseBuilder builder = Response.ok(content, type).type(type);
        if (name != null) {
            builder.header(HEADERNAME, name);
        }
        return builder.build();
    }

}
