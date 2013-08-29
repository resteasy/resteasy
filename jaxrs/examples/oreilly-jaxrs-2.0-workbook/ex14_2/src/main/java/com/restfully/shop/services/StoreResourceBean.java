package com.restfully.shop.services;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StoreResourceBean implements StoreResource
{
   public Response head(UriInfo uriInfo)
   {
      UriBuilder absolute = uriInfo.getBaseUriBuilder();
      URI customerUrl = absolute.clone().path("customers").build();
      URI orderUrl = absolute.clone().path("orders").build();
      URI productUrl = absolute.clone().path("products").build();
      javax.ws.rs.core.Link customers = javax.ws.rs.core.Link.fromUri(customerUrl).rel("customers").type("application/xml").build();
      javax.ws.rs.core.Link orders = javax.ws.rs.core.Link.fromUri(orderUrl).rel("orders").type("application/xml").build();
      javax.ws.rs.core.Link products = javax.ws.rs.core.Link.fromUri(productUrl).rel("products").type("application/xml").build();

      Response.ResponseBuilder builder = Response.ok().links(customers, orders, products);
      return builder.build();
   }
}
