package org.jboss.resteasy.test.cdi.inheritence.resource;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/")
@RequestScoped
public class CDIInheritenceInheritanceResource {
    @Inject
    private Logger log;

    @Inject
    @CDIInheritenceSelectBook
    private CDIInheritenceBook book;

    @GET
    @Path("vanilla")
    @Produces(MediaType.TEXT_PLAIN)
    public Response vanilla() {
        log.info("vanilla(): book is " + book.getClass());
        if (book.getClass().equals(CDIInheritenceBook.class)) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("alternative/vanilla")
    @Produces(MediaType.TEXT_PLAIN)
    public Response alternativeVanilla() {
        log.info("alternativeVanilla(): book is " + book.getClass());
        if (book.getClass().equals(CDIInheritenceBook.class)) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("alternative/selected")
    @Produces(MediaType.TEXT_PLAIN)
    public Response alternativeSelected() {
        log.info("alternativeSelected(): book is " + book.getClass());
        if (book.getClass().equals(CDIInheritenceBookSelectedAlternative.class)) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("specialized")
    @Produces(MediaType.TEXT_PLAIN)
    public Response specialized() {
        log.info("specialized(): book is " + book.getClass());
        if (book.getClass().equals(CDIInheritenceBookSpecialized.class)) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
