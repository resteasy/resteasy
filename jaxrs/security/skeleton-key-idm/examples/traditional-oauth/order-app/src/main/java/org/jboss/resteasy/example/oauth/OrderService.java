package org.jboss.resteasy.example.oauth;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/orders")
public class OrderService
{
   @POST
   public void order(@FormParam("product") String product)
   {
      System.out.println("Ordered: " + product);
   }
}
