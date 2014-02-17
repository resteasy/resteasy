package org.jboss.resteasy.resteasy767;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 28, 2013
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("sync")
   public Response sync()
   {
      return Response.ok().entity("sync").build();
   }
   
   @GET
   @Path("async/delay")
   public void asyncDelay(final @Suspend(10000) AsynchronousResponse response) throws Exception
   {
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               Thread.sleep(5000);
               Response jaxrs = Response.ok("async/delay").build();
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
   @Path("async/nodelay")
   public void asyncNoDelay(final @Suspend(10000) AsynchronousResponse response) throws Exception
   {
      Response jaxrs = Response.ok("async/nodelay").build();
      response.setResponse(jaxrs);
   }
   
}
