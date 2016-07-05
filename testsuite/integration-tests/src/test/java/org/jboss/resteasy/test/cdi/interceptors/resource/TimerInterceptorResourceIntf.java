package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.ejb.Local;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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