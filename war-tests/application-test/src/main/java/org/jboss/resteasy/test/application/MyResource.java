package org.jboss.resteasy.test.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("my")
public class MyResource
{
   public static int num_instatiations = 0;

   @Path("count")
   @GET
   @Produces("text/plain")
   public String getCount()
   {
      return Integer.toString(num_instatiations);
   }

   @Path("application/count")
   @GET
   @Produces("text/plain")
   public String getApplicationCount()
   {
      return Integer.toString(MyApplication.num_instantiations);
   }

   @Path("exception")
   @GET
   @Produces("text/plain")
   public String getException()
   {
      throw new FooException();
   }
}
