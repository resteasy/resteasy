package org.jboss.resteasy.test.cdi.basic.resource;


import org.jboss.resteasy.test.cdi.util.Constants;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public interface EJBResourceParent {
    @GET
    @Path("verifyScopes")
    int verifyScopes();

    @GET
    @Path("verifyInjection")
    int verifyInjection();

    @POST
    @Path("create")
    @Consumes(Constants.MEDIA_TYPE_TEST_XML)
    int createBook(EJBBook book);

    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces(Constants.MEDIA_TYPE_TEST_XML)
    EJBBook lookupBookById(@PathParam("id") int id);

    @GET
    @Path("uses/{count}")
    int testUse(@PathParam("count") int count);

    @GET
    @Path("reset")
    void reset();
}

