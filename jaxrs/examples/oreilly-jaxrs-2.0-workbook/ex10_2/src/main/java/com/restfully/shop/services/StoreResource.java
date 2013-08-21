package com.restfully.shop.services;

import com.restfully.shop.domain.Link;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

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
      String customerUrl = absolute.clone().path("customers").build().toString();
      String orderUrl = absolute.clone().path("orders").build().toString();

      Response.ResponseBuilder builder = Response.ok();
      builder.header("Link", new Link("customers", customerUrl, "application/xml"));
      builder.header("Link", new Link("orders", orderUrl, "application/xml"));
      return builder.build();
   }
}
