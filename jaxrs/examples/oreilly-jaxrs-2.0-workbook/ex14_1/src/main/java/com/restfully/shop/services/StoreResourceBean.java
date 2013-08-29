package com.restfully.shop.services;

import javax.ejb.Stateless;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Stateless
public class StoreResourceBean implements StoreResource
{
   public Response head(UriInfo uriInfo)
   {
      UriBuilder absolute = uriInfo.getBaseUriBuilder();
      URI customerUrl = absolute.clone().path("customers").build();
      URI orderUrl = absolute.clone().path("orders").build();
      URI productUrl = absolute.clone().path("products").build();
      Link customers = Link.fromUri(customerUrl).rel("customers").type("application/xml").build();
      Link orders = Link.fromUri(orderUrl).rel("orders").type("application/xml").build();
      Link products = Link.fromUri(productUrl).rel("products").type("application/xml").build();

      Response.ResponseBuilder builder = Response.ok().links(customers, orders, products);
      return builder.build();
   }
}
