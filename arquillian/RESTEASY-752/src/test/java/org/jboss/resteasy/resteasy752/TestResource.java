package org.jboss.resteasy.resteasy752;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Matt Van Wely
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2012
 */
@Path("/")
public class TestResource
{
   public static final int SERVLET_TIMEOUT = 10000;
   public static final int SERVLET_EXTRA_TIME = 10000;
   
   private static final Logger log = LoggerFactory.getLogger(TestResource.class);
   private static int counter = 0;
   private ExecutorService _executor = Executors.newSingleThreadExecutor();

   public TestResource()
   {
      log.info("console, in ctor()");
   }

   @GET
   @Path("/timeoutStacks")
   public void timeoutStacksMultipleResponses(@Suspend(SERVLET_TIMEOUT) final AsynchronousResponse response) throws InterruptedException
   {
      Runnable runner = new Runnable()
      {
         public void run()
         {
            // force timeout every other call
            if (counter++ % 2 == 0)
            {
               try
               {
                  int sleeper = SERVLET_TIMEOUT + SERVLET_EXTRA_TIME;
                  log.info(this + " forcing timeout by sleeping for " + sleeper + "ms: " + new Date());
                  Thread.sleep(sleeper);
                  log.info(this + " woke up: " + new Date());
               } catch (InterruptedException e)
               {
                  log.info("interrupted: " + Thread.interrupted());
               }
            }

            try
            {
               log.info(this + " writing response");
               Date date = new Date();
               response.setResponse(Response.ok(this + " string returned at " + date + "\n").build());
            } catch (Exception e)
            {
               log.debug("failed setting response", e);
            }
         }
      };

      _executor.submit(runner);
   }

}
