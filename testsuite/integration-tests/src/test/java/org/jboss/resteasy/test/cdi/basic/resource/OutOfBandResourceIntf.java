package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Local;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ws.rs.core.Response;

@Local
public interface OutOfBandResourceIntf {
   Response scheduleTimer();

   Response testTimer() throws InterruptedException;

   @Timeout
   void timeout(Timer timer);
}
