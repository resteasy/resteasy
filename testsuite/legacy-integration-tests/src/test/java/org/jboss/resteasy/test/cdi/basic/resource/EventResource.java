package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.test.cdi.util.Constants;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
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
public class EventResource {
    private static Map<Integer, EJBBook> collection = new HashMap<Integer, EJBBook>();
    private static AtomicInteger counter = new AtomicInteger();

    @Inject
    @EventsProcess
    Event<String> processEvent;

    @Inject
    @EventsRead(context = "resource")
    @EventsProcess
    Event<String> readProcessEvent;

    @Inject
    @EventsWrite(context = "resource")
    @EventsProcess
    Event<String> writeProcessEvent;

    @Inject
    EventsBookReader bookReader;

    @Inject
    private Logger log;

    @POST
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        log.info("entering EventResource.test()");
        log.info("event list:");
        ArrayList<Object> eventList = bookReader.getEventList();
        for (int i = 0; i < eventList.size(); i++) {
            log.info(eventList.get(i).toString());
        }

        ArrayList<String> expectedEvents = new ArrayList<>();
        expectedEvents.add("readInterceptEvent");
        expectedEvents.add("readInterceptEvent");
        expectedEvents.add("readEvent");
        expectedEvents.add("readEvent");
        expectedEvents.add("processEvent");
        expectedEvents.add("readProcessEvent");
        expectedEvents.add("readProcessEvent");
        expectedEvents.add("writeInterceptEvent");
        expectedEvents.add("writeInterceptEvent");
        expectedEvents.add("processEvent");
        expectedEvents.add("writeProcessEvent");
        expectedEvents.add("writeProcessEvent");
        expectedEvents.add("writeInterceptEvent");
        expectedEvents.add("writeInterceptEvent");
        expectedEvents.add("writeEvent");
        expectedEvents.add("writeEvent");

        boolean status = true;
        if (!(eventList.size() == expectedEvents.size())) {
            status = false;
            log.info(String.format("EventList should have %d events, not %d", expectedEvents.size(), eventList.size()));
        }

        for (int i = 0; i < Math.min(expectedEvents.size(), eventList.size()); i++) {
            if (!expectedEvents.get(i).equals(eventList.get(i))) {
                status = false;
                log.info(String.format("%d. position: %s is found, %s is expected", i, eventList.get(i), expectedEvents.get(i)));
            }
        }

        log.info("leaving EventResource.test()");
        return status ? Response.ok().build() : Response.serverError().build();
    }

    @POST
    @Path("create")
    @Consumes(Constants.MEDIA_TYPE_TEST_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createBook(EJBBook book) {
        log.info("entering EventResource.createBook()");
        log.info("EventResource firing processEvent");
        processEvent.fire("processEvent");
        int id = counter.getAndIncrement();
        book.setId(id);
        collection.put(id, book);
        log.info("stored: " + id + "->" + book);
        log.info("EventResource firing readProcessEvent");
        readProcessEvent.fire("readProcessEvent");
        log.info("leaving EventResource.createBook()");
        return Response.ok(id).build();
    }

    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces(Constants.MEDIA_TYPE_TEST_XML)
    public EJBBook lookupBookById(@PathParam("id") int id) {
        log.info("entering EventResource.lookupBookById(" + id + ")");
        log.info("books: " + collection);
        log.info("EventResource firing processEvent");
        processEvent.fire("processEvent");
        EJBBook book = collection.get(id);
        log.info("EventResource firing writeProcessEvent");
        writeProcessEvent.fire("writeProcessEvent");
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("leaving EventResource.lookupBookById(" + id + ")");
        return book;
    }
}
