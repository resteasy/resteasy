package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Stateless
public class EJBEventsSourceImpl implements EJBEventsSource {
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
    EJBEventsObserver eventObserver;

    @Inject
    private Logger log;

    public boolean test() {
        log.info("entering EJBEventsSourceImpl.test()");
        ArrayList<Object> eventList = eventObserver.getEventList();
        for (int i = 0; i < eventList.size(); i++) {
            log.info(eventList.get(i).toString());
        }
        log.info("leaving EJBEventsSourceImpl.test()");
        return true;
    }

    public int createBook(EJBBook book) {
        log.info("entering EJBEventsSourceImpl.createBook()");
        log.info("EJBEventsSourceImpl firing processEvent");
        processEvent.fire("processEvent");
        int id = counter.getAndIncrement();
        book.setId(id);
        collection.put(id, book);
        log.info("stored: " + id + "->" + book);
        log.info("EJBEventsSourceImpl firing readProcessEvent");
        readProcessEvent.fire("readProcessEvent");
        log.info("leaving EJBEventsSourceImpl.createBook()");
        return id;
    }

    public EJBBook lookupBookById(@PathParam("id") int id) {
        log.info("entering EJBEventsSourceImpl.lookupBookById(" + id + ")");
        log.info("books: " + collection);
        log.info("EJBEventsSourceImpl firing processEvent");
        processEvent.fire("processEvent");
        EJBBook book = collection.get(id);
        log.info("EJBEventsSourceImpl firing writeProcessEvent");
        writeProcessEvent.fire("writeProcessEvent");
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("leaving EJBEventsSourceImpl.lookupBookById(" + id + ")");
        return book;
    }
}
