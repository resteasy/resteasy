package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.ArrayList;
import java.util.logging.Logger;

import jakarta.ejb.Stateful;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@Stateful
public class EJBEventsObserverImpl implements EJBEventsObserver {
    @Inject
    @EventsRead(context = "reader")
    Event<String> readEvent;

    @Inject
    private Logger log;

    private ArrayList<Object> eventList = new ArrayList<Object>();

    public void process(@Observes @EventsProcess String event) {
        eventList.add(event);
        log.info("EJBEventsObserverImpl.process() got " + event);
    }

    public void processRead(@Observes @EventsProcess @EventsRead(context = "resource") String event) {
        eventList.add(event);
        log.info("EJBEventsObserverImpl.processRead() got " + event);
    }

    public void processWrite(@Observes @EventsProcess @EventsWrite(context = "resource") String event) {
        eventList.add(event);
        log.info("EJBEventsObserverImpl.processWrite() got " + event);
    }

    public ArrayList<Object> getEventList() {
        return new ArrayList<Object>(eventList);
    }
}
