package org.jboss.resteasy.examples.asyncjob;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/resource")
public class MyResource
{
   private static int count = 0;


   @POST
   @Produces("text/plain")
   @Consumes("text/plain")
   public String post(String content) throws Exception
   {
      Thread.sleep(1000);
      return content;
   }

   @GET
   @Produces("text/plain")
   public String get()
   {
      return Integer.toString(count);
   }

   @PUT
   @Consumes("text/plain")
   public void put(String content) throws Exception
   {
      System.out.println("IN PUT!!!!");
      Thread.sleep(1000);
      System.out.println("******* countdown complete ****");
      count++;
   }
}
