package org.jboss.resteasy.test.smoke;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleSubresource
{
   @GET
   @Path("basic")
   @ProduceMime("text/plain")
   public String getBasic()
   {
      return "basic";
   }

   @Path("subresource")
   public SimpleSubresource getSubresource() {
      System.out.println("Subsubresource");
      return new SimpleSubresource();
   }

}
