package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundTimeout;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("timer")
@Stateless
public class TimerInterceptorResource implements TimerInterceptorResourceIntf {
    private static final String TIMER_INFO = "timerInfo";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static boolean timerExpired;
    private static boolean timerInterceptorInvoked;

    @Inject
    private Logger log;

    @Resource
    private SessionContext ctx;
    private TimerService timerService;

    @Override
    @GET
    @Path("schedule")
    public Response scheduleTimer() {
        log.info("entering scheduleTimer()");
        timerService = ctx.getTimerService();
        if (timerService != null) {
            timerService.createTimer(1000, TIMER_INFO);
            log.info("timer scheduled");
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
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
