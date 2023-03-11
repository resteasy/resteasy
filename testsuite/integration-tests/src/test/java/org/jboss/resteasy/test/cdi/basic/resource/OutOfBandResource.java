package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.CountDownLatch;

import jakarta.annotation.Resource;
import jakarta.ejb.Schedule;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerService;
import jakarta.interceptor.AroundTimeout;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("timer")
@Stateless
public class OutOfBandResource implements OutOfBandResourceIntf {
    private static final Logger log = Logger.getLogger(OutOfBandResource.class);
    private static final String TIMER_INFO = "timerInfo";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static boolean timerExpired;
    private static boolean timerInterceptorInvoked;

    @Resource
    private SessionContext ctx;
    @Resource
    private TimerService timerService;

    @Override
    @GET
    @Path("schedule")
    public Response scheduleTimer() {
        timerService = ctx.getTimerService();
        if (timerService != null) {
            timerService.createTimer(1000, TIMER_INFO);
            log.info("timer scheduled");
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Schedule(second = "1")
    public void automaticTimeout(Timer timer) {
        log.info("entering automaticTimeout()");
        timerExpired = true;
        latch.countDown();
    }

    @Override
    @GET
    @Path("test")
    public Response testTimer() throws InterruptedException {
        log.info("entering testTimer()");
        latch.await();
        if (!timerInterceptorInvoked) {
            return Response.serverError().entity("timerInterceptorInvoked == false").build();
        }
        if (!timerExpired) {
            return Response.serverError().entity("timerExpired == false").build();
        }
        return Response.ok().build();
    }

    @Override
    @Timeout
    public void timeout(Timer timer) {
        log.info("entering timeout()");
        if (TIMER_INFO.equals(timer.getInfo())) {
            timerExpired = true;
            latch.countDown();
        }
    }

    @AroundTimeout
    public Object aroundTimeout(InvocationContext ctx) throws Exception {
        timerInterceptorInvoked = true;
        log.info("aroundTimeout() invoked");
        return ctx.proceed();
    }
}
