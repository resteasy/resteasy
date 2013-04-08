package org.jboss.resteasy.test.async;

import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class MyResource
{
   @GET
   @Produces("text/plain")
   public void get(final @Suspend(2000) AsynchronousResponse response)
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
               response.setResponse(jaxrs);
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
   public void timeout(final @Suspend(10) AsynchronousResponse response)
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
               response.setResponse(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }
}