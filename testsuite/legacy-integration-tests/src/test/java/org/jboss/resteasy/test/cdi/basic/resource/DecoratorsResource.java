package org.jboss.resteasy.test.cdi.basic.resource;


import org.jboss.resteasy.test.cdi.util.Constants;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Path("/")
@RequestScoped
public class DecoratorsResource implements DecoratorsResourceIntf {
    private static Map<Integer, EJBBook> collection = new HashMap<Integer, EJBBook>();
    private static AtomicInteger counter = new AtomicInteger();

    @Inject
    private Logger log;

    @Override
    @POST
    @Path("create")
    @Consumes(Constants.MEDIA_TYPE_TEST_XML)
    @Produces(MediaType.TEXT_PLAIN)
    @DecoratorsFilterBinding
    @DecoratorsResourceBinding
    public Response createBook(EJBBook book) {
        log.info("entering DecoratorsResource.createBook()");
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_ENTER);
        int id = counter.getAndIncrement();
        book.setId(id);
        collection.put(id, book);
        log.info("stored: " + id + "->" + book);
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_LEAVE);
        log.info("leaving DecoratorsResource.createBook()");
        return Response.ok(id).build();
    }

    @Override
    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces(Constants.MEDIA_TYPE_TEST_XML)
    @DecoratorsFilterBinding
    @DecoratorsResourceBinding
    public EJBBook lookupBookById(@PathParam("id") int id) {
        log.info("entering DecoratorsResource.lookupBookById(" + id + ")");
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_ENTER);
        log.info("books: " + collection);
        EJBBook book = collection.get(id);
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_LEAVE);
        log.info("leaving DecoratorsResource.lookupBookById(" + id + ")");
        return book;
    }

    @Override
    @POST
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        log.info("entering DecoratorsResource.test()");
        ArrayList<String> expectedList = new ArrayList<String>();

        // Call to createBook()
        expectedList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.READER_INTERCEPTOR_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.READER_INTERCEPTOR_ENTER);
        expectedList.add(DecoratorsVisitList.READER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.READER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.READER_INTERCEPTOR_LEAVE);
        expectedList.add(DecoratorsVisitList.READER_INTERCEPTOR_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_ENTER);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_LEAVE);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);

        // Call to lookupBookById()
        expectedList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.REQUEST_FILTER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_ENTER);
        expectedList.add(DecoratorsVisitList.RESOURCE_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESOURCE_INTERCEPTOR_LEAVE);
        expectedList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_ENTER);
        expectedList.add(DecoratorsVisitList.WRITER_DECORATOR_ENTER);
        expectedList.add(DecoratorsVisitList.WRITER_DECORATOR_LEAVE);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_LEAVE);
        expectedList.add(DecoratorsVisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);

        ArrayList<String> visitList = DecoratorsVisitList.getList();
        boolean status = expectedList.size() == visitList.size();
        if (!status) {
            log.info("expectedList.size() [" + expectedList.size() + "] != visitList.size() [" + visitList.size() + "]");
        } else {
            for (int i = 0; i < expectedList.size(); i++) {
                if (!expectedList.get(i).equals(visitList.get(i))) {
                    status = false;
                    log.info("visitList.get(" + i + ") incorrect: should be: " + expectedList.get(i) + ", is: " + visitList.get(i));
                    break;
                }
            }
        }
        if (!status) {
            log.info("\rexpectedList: ");
            for (int i = 0; i < expectedList.size(); i++) {
                log.info(i + ": " + expectedList.get(i).toString());
            }
            log.info("\rvisitList:");
            for (int i = 0; i < visitList.size(); i++) {
                log.info(i + ": " + visitList.get(i).toString());
            }
        }
        log.info("leaving DecoratorsResource.test()");
        return status ? Response.ok().build() : Response.serverError().build();
    }
}
