package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DateFormatPathTest
{
   @Path("/")
   public static class DateResource
   {
      @Path("/widget/{date}")
      @GET
      @Produces("text/plain")
      public String get(@PathParam("date") String date)
      {
         return date;
      }
   }


   @Test
   public void testDate() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(DateResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/widget/08%2F26%2F2009");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(200, response.getStatus());

         // XXX Should this be SC_NOT_FOUND?
         // Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
         Assert.assertEquals("08/26/2009", response.getContentAsString());
      }

   }

}
