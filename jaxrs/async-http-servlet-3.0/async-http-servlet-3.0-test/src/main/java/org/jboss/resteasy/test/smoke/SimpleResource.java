package org.jboss.resteasy.test.smoke;

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
public class SimpleResource
{

   @GET
   @Path("basic")
   @Produces("text/plain")
   public void getBasic(final @Suspend(100000) AsynchronousResponse response) throws Exception
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

   @GET
   @Path("xml")
   @Produces("application/xml")
   public Customer getCustomer()
   {
      return new Customer("Bill Burke");
   }


}