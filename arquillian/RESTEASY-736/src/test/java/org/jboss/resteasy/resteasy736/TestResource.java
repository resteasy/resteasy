package org.jboss.resteasy.resteasy736;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 15, 2012
 */
@Path("/")
@Produces("text/plain")
public class TestResource
{
   @GET
   @Path("test")
   public void test(final @Suspend(5000) AsynchronousResponse response)
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("TestResource test async thread started, timeout 5000, sleep 10000");
               Thread.sleep(10000);
               Response jaxrs = Response.ok("test").type(MediaType.TEXT_PLAIN).build();
               response.setResponse(jaxrs);
               System.out.println("TestResource test async thread finished");
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
   @Path("default")
   public void defalt(final @Suspend AsynchronousResponse response)
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               int millis = getDefaultTimeout() + 5000;
               System.out.println("TestResource default async thread started, timeout default, sleep " + millis);
               Thread.sleep(millis);
               Response jaxrs = Response.ok("test").type(MediaType.TEXT_PLAIN).build();
               response.setResponse(jaxrs);
               System.out.println("TestResource default async thread finished");
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }

   public static int getDefaultTimeout() {
     // Jetty async timeout defaults to 30000
     // Tomcat async timeout defaults to 10000
     // JBoss async timeout defaults to 60000
     return 60000;
   }
}
