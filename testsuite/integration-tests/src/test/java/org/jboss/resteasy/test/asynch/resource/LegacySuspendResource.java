package org.jboss.resteasy.test.asynch.resource;

import org.jboss.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

@Path("/")
public class LegacySuspendResource {
   private static Logger logger = Logger.getLogger(LegacySuspendResource.class);

   @GET
   @Produces("text/plain")
   public void get(@Suspended final AsyncResponse response) {
      response.setTimeout(8000, TimeUnit.MILLISECONDS);
      Thread t = new Thread() {
         @Override
         public void run() {
            try {
               Thread.sleep(100);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            } catch (Exception e) {
               StringWriter errors = new StringWriter();
               e.printStackTrace(new PrintWriter(errors));
               logger.error(errors.toString());
            }
         }
      };
      t.start();
   }

   @GET
   @Path("timeout")
   @Produces("text/plain")
   public void timeout(@Suspended final AsyncResponse response) {
      response.setTimeout(100, TimeUnit.MILLISECONDS);
      Thread t = new Thread() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            } catch (Exception e) {
               StringWriter errors = new StringWriter();
               e.printStackTrace(new PrintWriter(errors));
               logger.error(errors.toString());
            }
         }
      };
      t.start();
   }

}
