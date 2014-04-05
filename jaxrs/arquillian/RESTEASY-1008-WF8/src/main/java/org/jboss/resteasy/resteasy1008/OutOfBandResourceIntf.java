package org.jboss.resteasy.resteasy1008;

import javax.ejb.Local;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Local
public interface OutOfBandResourceIntf
{
//   @GET
//   @Path("timer/schedule")
   public abstract Response scheduleTimer();

//   @GET
//   @Path("timer/test")
   public abstract Response testTimer() throws InterruptedException;

   @Timeout
   public abstract void timeout(Timer timer);
}