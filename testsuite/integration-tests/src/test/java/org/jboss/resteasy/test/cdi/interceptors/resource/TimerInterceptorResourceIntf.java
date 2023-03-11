package org.jboss.resteasy.test.cdi.interceptors.resource;

import jakarta.ejb.Local;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Local
public interface TimerInterceptorResourceIntf {
    @GET
    @Path("timer/schedule")
    Response scheduleTimer();

    @GET
    @Path("timer/test")
    Response testTimer() throws InterruptedException;

    @Timeout
    void timeout(Timer timer);
}
