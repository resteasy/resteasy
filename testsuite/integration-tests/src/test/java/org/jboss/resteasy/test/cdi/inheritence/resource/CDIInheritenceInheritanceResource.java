package org.jboss.resteasy.test.cdi.inheritence.resource;

import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
