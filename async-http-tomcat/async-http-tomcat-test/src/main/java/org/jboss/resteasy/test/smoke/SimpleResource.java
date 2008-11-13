package org.jboss.resteasy.test.smoke;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.junit.Assert;
import java.net.URI;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class SimpleResource
{
   private static int count = 0;

   @GET
   @Path("basic")
   @Produces("text/plain")
   public void getBasic(final @Suspend(10000) AsynchronousResponse response) throws Exception
   {
               Thread t = new Thread()
               {
                  @Override
                  public void run()
                  {
                     try
                     {
                        System.out.println("STARTED!!!!");
                        Thread.sleep(5000);
                        Response jaxrs = Response.ok("basic").type(MediaType.TEXT_PLAIN).build();
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