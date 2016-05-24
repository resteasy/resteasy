package org.jboss.resteasy.test.finegrain.methodparams;

import junit.framework.Assert;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Simon Ström on 7/11/14.
 */
public class QueryTest{
   public static class SearchQuery
   {

      @QueryParam("term")
      private String term;

      @QueryParam("order")
      private String order;

      @QueryParam("limit")
      private String limit;

      @Override
      public String toString()
      {
         return new StringBuilder("term: '").append(term).append("', order: '").append(order).append("', limit: '").append(limit).append("'").toString();
      }
   }

   @Path("search")
   public static class MyResource
   {
      @GET
      @Produces(MediaType.TEXT_PLAIN)
      @Consumes(MediaType.TEXT_PLAIN)
      public String get(@Query SearchQuery searchQuery)
      {
         return searchQuery.toString();
      }
   }

   @Test
   public void testQueryParamPrefix() throws Exception {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(MyResource.class));

      MockHttpRequest request = MockHttpRequest.get("/search?term=t1&order=ASC");
      MockHttpResponse response = new MockHttpResponse();

      dispatcher.invoke(request, response);

      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("term: 't1', order: 'ASC', limit: 'null'", response.getContentAsString());
   }
}
