package org.jboss.resteasy.test;

import org.jboss.logging.Logger;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/jaxrs")
public class AsyncJaxrsResource
{
   protected boolean cancelled;
   private static final Logger logger = Logger.getLogger(AsyncJaxrsResource.class);

   @GET
   @Path("resume/object")
   @Produces("application/xml")
   public void resumeObject(@Suspended final AsyncResponse response) {
      response.resume(new XmlData("bill"));
   }

   @GET
   @Path("resume/object/thread")
   @Produces("application/xml")
   public void resumeObjectThread(@Suspended final AsyncResponse response) throws Exception
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            response.resume(new XmlData("bill"));
         }
      };
      t.start();
   }

   @GET
   @Path("injection-failure/{param}")
   public void injectionFailure(@Suspended final AsyncResponse response, @PathParam("param") int id) {
      logger.debug("injectionFailure: " + id);
      throw new ForbiddenException("Should be unreachable");
   }

   @GET
   @Path("method-failure")
   public void injectionFailure(@Suspended final AsyncResponse response) {
      throw new ForbiddenException("Should be unreachable");
   }

   @GET
   @Path("cancelled")
   public Response getCancelled()
   {
      if (cancelled) return Response.noContent().build();
      else return Response.status(500).build();
   }

   @PUT
   @Path("cancelled")
   public void resetCancelled()
   {
      cancelled = false;
   }

   @GET
   @Produces("text/plain")
   public void get(@Suspended final AsyncResponse response) throws Exception
   {
      response.setTimeout(200000, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               Thread.sleep(100);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               logger.error(e.getMessage(), e);
            }
         }
      };
      t.start();
   }

   @GET
   @Path("empty")
   @Produces("text/plain")
   public void getEmpty(@Suspended final AsyncResponse response) throws Exception
   {
      response.setTimeout(200000, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               Thread.sleep(100);
               response.resume(Response.noContent().build());
            }
            catch (Exception e)
            {
               logger.error(e.getMessage(), e);
            }
         }
      };
      t.start();
   }

   @GET
   @Path("timeout")
   @Produces("text/plain")
   public void timeout(@Suspended final AsyncResponse response)
   {
      response.setTimeout(10, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               Thread.sleep(100000);
               Response jaxrs = Response.ok("goodbye").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               logger.error(e.getMessage(), e);
            }
         }
      };
      t.start();
   }

   @GET
   @Path("cancel")
   @Produces("text/plain")
   public void cancel(@Suspended final AsyncResponse response) throws Exception
   {
      logger.debug("entering cancel()");
      response.setTimeout(10000, TimeUnit.MILLISECONDS);
      final CountDownLatch sync = new CountDownLatch(1);
      final CountDownLatch ready = new CountDownLatch(1);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               logger.debug("cancel(): starting thread");
               sync.countDown();
               ready.await();
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               logger.debug("SETTING CANCELLED");
               cancelled = !response.resume(jaxrs);
               logger.debug("cancelled: " + cancelled);
            }
            catch (Exception e)
            {
               logger.error(e.getMessage(), e);
            }
         }
      };
      t.start();

      sync.await();
      logger.debug("cancel(): cancelling response");
      response.cancel();
      ready.countDown();
      Thread.sleep(1000);
   }

}
