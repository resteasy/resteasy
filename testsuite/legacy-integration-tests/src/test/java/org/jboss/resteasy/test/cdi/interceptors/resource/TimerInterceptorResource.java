package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

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
