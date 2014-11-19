package org.jboss.resteasy.resteasy1125;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
public class AbstractResource<T> {

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createEntity(T entity) {
        return Response.ok("Success!").build();
    }

}
