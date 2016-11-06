package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.QueryParam;

public class QuerySearchQuery {

   @QueryParam("term")
   private String term;

   @QueryParam("order")
   private String order;

   @QueryParam("limit")
   private String limit;

   @Override
   public String toString() {
      return new StringBuilder("term: '").append(term).append("', order: '").append(order).append("', limit: '").append(limit).append("'").toString();
   }
}
