package com.restfully.shop.services;

import com.restfully.shop.domain.AtomLink;
import com.restfully.shop.domain.AtomLink;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/shop")
public class StoreResource
{
   @HEAD
   public Response head(@Context UriInfo uriInfo)
   {
      UriBuilder absolute = uriInfo.getBaseUriBuilder();
      URI customerUrl = absolute.clone().path(CustomerResource.class).build();
      URI orderUrl = absolute.clone().path(OrderResource.class).build();

      Response.ResponseBuilder builder = Response.ok();
      Link customers = Link.fromUri(customerUrl).rel("customers").title("application/xml").build();
      Link orders = Link.fromUri(orderUrl).rel("orders").title("application/xml").build();
      builder.links(customers, orders);
      return builder.build();
   }
}
