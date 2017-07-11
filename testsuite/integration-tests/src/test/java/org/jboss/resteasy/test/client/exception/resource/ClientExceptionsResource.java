package org.jboss.resteasy.test.client.exception.resource;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/")
public class ClientExceptionsResource {
    @POST
    @Path("post")
    public Response post(ClientExceptionsData data) {
        return Response.ok().entity(data).build();
    }

    @GET
    @Path("get")
    public String get() {
        return "OK";
    }

    @Path("data")
    @GET
    public Response getData(@Context HttpHeaders headers) {
        Response response = Response.ok()
                .type(headers.getAcceptableMediaTypes().get(0))
                .entity(new ClientExceptionsData("test", "test"))
                .build();
        return response;
    }

    @Path("senddata")
    @POST
    public ClientExceptionsData postandget(ClientExceptionsData data) {
        return data;
    }

    @Path("empty")
    @GET
    public Response getEmpty(@Context HttpHeaders headers) {
        Response response = Response.ok()
                .type(headers.getAcceptableMediaTypes().get(0))
                .header(HttpHeaders.CONTENT_LENGTH, 0)
                .build();
        return response;
    }

    @GET
    @Path("error")
    public Response error() {
        throw new WebApplicationException(HttpResponseCodes.SC_FORBIDDEN);
    }
}
