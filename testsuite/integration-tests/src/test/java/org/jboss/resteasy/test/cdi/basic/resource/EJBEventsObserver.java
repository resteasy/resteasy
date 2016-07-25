package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import javax.enterprise.event.Observes;
import java.util.ArrayList;

@Local
public interface EJBEventsObserver {
    void process(@Observes @EventsProcess String event);

    void processRead(@Observes @EventsProcess @EventsRead(context = "resource") String event);

    void processWrite(@Observes @EventsProcess @EventsWrite(context = "resource") String event);

    ArrayList<Object> getEventList();
}
