package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.ArrayList;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface EJBEventsObserver {
    void process(@Observes @EventsProcess String event);

    void processRead(@Observes @EventsProcess @EventsRead(context = "resource") String event);

    void processWrite(@Observes @EventsProcess @EventsWrite(context = "resource") String event);

    ArrayList<Object> getEventList();
}
