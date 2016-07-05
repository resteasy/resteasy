package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ws.rs.core.Response;

@Local
public interface OutOfBandResourceIntf {
    Response scheduleTimer();

    Response testTimer() throws InterruptedException;

    @Timeout
    void timeout(Timer timer);
}