package org.resteasy.test.finegrain.methodparams;

import org.junit.Assert;
import org.junit.Test;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.MockDispatcherFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.io.IOException;

/**
 * Test that a locator and resource with same path params work
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipleMatrixSegmentsTest
{
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
      HttpServletDispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addResource(Resource.class);
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
         request.setPathInfo(path);
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }

   }

}
