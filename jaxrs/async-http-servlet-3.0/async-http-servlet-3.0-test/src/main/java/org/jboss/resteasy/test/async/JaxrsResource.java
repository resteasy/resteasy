package org.jboss.resteasy.test.async;


import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Suspend;
import javax.ws.rs.core.AsynchronousResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/jaxrs")
public class JaxrsResource
{
   protected boolean cancelled;

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
   @Suspend(timeOut = 2000)
   public void get(final AsynchronousResponse response)
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("STARTED!!!!");
               Thread.sleep(100);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }

   @GET
   @Path("timeout")
   @Produces("text/plain")
   @Suspend(timeOut=10)
   public void timeout(final AsynchronousResponse response)
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("STARTED!!!!");
               Thread.sleep(100000);
               Response jaxrs = Response.ok("goodbye").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }
   @GET
   @Path("timeout/fallback")
   @Produces("text/plain")
   @Suspend(timeOut = 100)
   public void timeoutFallback(final AsynchronousResponse response)
   {
      response.setFallbackResponse(Response.status(400).build());
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("STARTED!!!!");
               Thread.sleep(100000);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }

   @GET
   @Path("cancel")
   @Produces("text/plain")
   @Suspend(timeOut = 10000)
   public void cancel(final AsynchronousResponse response) throws Exception
   {
      final CountDownLatch sync = new CountDownLatch(1);
      final CountDownLatch ready = new CountDownLatch(1);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               sync.countDown();
               System.out.println("awaiting thread");
               ready.await();
               System.out.println("resuming");
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (IllegalStateException ie)
            {
               System.out.println("cancel caused exception");
               cancelled = true;
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();

      sync.await();
      response.cancel();
      ready.countDown();
   }

}