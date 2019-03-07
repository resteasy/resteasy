package org.jboss.resteasy.test.asynch.resource;


import org.jboss.logging.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/jaxrs")
public class JaxrsAsyncServletJaxrsResource {
   private Logger logger = Logger.getLogger(JaxrsAsyncServletJaxrsResource.class);
   protected volatile boolean cancelled;

   @GET
   @Path("resume/object")
   @Produces("application/xml")
   public void resumeObject(@Suspended final AsyncResponse response) {
      response.resume(new JaxrsAsyncServletXmlData("bill"));
   }

   @GET
   @Path("resume/object/thread")
   @Produces("application/xml")
   public void resumeObjectThread(@Suspended final AsyncResponse response) throws Exception {
      Thread t = new Thread() {
            @Override
            public void run() {
            response.resume(new JaxrsAsyncServletXmlData("bill"));
            }
      };
      t.start();
   }


   @GET
   @Path("injection-failure/{param}")
   public void injectionFailure(@Suspended final AsyncResponse response, @PathParam("param") int id) {
      throw new ForbiddenException("Should be unreachable");
   }

   @GET
   @Path("method-failure")
   public void injectionFailure(@Suspended final AsyncResponse response) {
      throw new ForbiddenException("Should be unreachable");
   }


   @GET
   @Path("cancelled")
   public Response getCancelled() {
      if (cancelled) {
         return Response.noContent().build();
      } else {
         return Response.status(500).build();
      }
   }

   @PUT
   @Path("cancelled")
   public void resetCancelled() {
      cancelled = false;
   }

   @GET
   @Produces("text/plain")
   public void get(@Suspended final AsyncResponse response) throws Exception {
      response.setTimeout(2000, TimeUnit.MILLISECONDS);
      Thread t = new Thread() {
            private Logger logger = Logger.getLogger(JaxrsAsyncServletJaxrsResource.class);
            @Override
            public void run() {
            try {
               logger.info("STARTED!!!!");
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
      response.setTimeout(10, TimeUnit.MILLISECONDS);
      Thread t = new Thread() {
            private Logger logger = Logger.getLogger(JaxrsAsyncServletJaxrsResource.class);
            @Override
            public void run() {
            try {
               logger.info("STARTED!!!!");
               Thread.sleep(100000);
               Response jaxrs = Response.ok("goodbye").type(MediaType.TEXT_PLAIN).build();
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
   @Path("cancel")
   @Produces("text/plain")
   public void cancel(@Suspended final AsyncResponse response) throws Exception {
      response.setTimeout(10000, TimeUnit.MILLISECONDS);
      final CountDownLatch sync = new CountDownLatch(1);
      final CountDownLatch ready = new CountDownLatch(1);
      Thread t = new Thread() {
            private Logger logger = Logger.getLogger(JaxrsAsyncServletJaxrsResource.class);
            @Override
            public void run() {
            try {
               sync.countDown();
               logger.info("cancel awaiting thread");
               ready.await();
               logger.info("cancel resuming");
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               cancelled = !response.resume(jaxrs);
            } catch (Exception e) {
               StringWriter errors = new StringWriter();
               e.printStackTrace(new PrintWriter(errors));
               logger.error(errors.toString());
            }
            }
      };
      t.start();

      sync.await();
      logger.info("Cancelling...");
      response.cancel();
      ready.countDown();
   }

}
