package org.resteasy.test.finegrain.methodparams;

import org.junit.Assert;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.mock.MockDispatcherFactory;
import org.resteasy.mock.MockHttpRequest;
import org.resteasy.mock.MockHttpResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;

/**
 * Test that a locator and resource with same path params work
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipleMatrixSegmentsTest
{
   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/{parent}/children/{child}")
      public String get(@PathParam("parent")PathSegment parent, @PathParam("child")PathSegment child)
      {
         Assert.assertEquals("bill", parent.getMatrixParameters().getFirst("name"));
         Assert.assertEquals("111", parent.getMatrixParameters().getFirst("ssn"));
         Assert.assertEquals("skippy", child.getMatrixParameters().getFirst("name"));
         Assert.assertEquals("3344", child.getMatrixParameters().getFirst("ssn"));
         return "content";
      }
   }

   @Test
   public void testMultiple() throws Exception
   {
      String path = "/;name=bill;ssn=111/children/;name=skippy;ssn=3344";
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Resource.class);
      MockHttpRequest request = MockHttpRequest.get(path);
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
   }

}
