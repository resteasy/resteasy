package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Test that empty QueryParam list is empty
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CollectionDefaultValueTest extends BaseResourceTest
{
   @Path("/collection")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public String get(@QueryParam("nada") List<String> params)
      {
         Assert.assertNotNull(params);
         Assert.assertEquals(0, params.size());
         return "hello";
      }

   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(MyResource.class);
   }

   @Test
   public void testEmpty() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/collection"));
      Assert.assertEquals(200, request.get().getStatus());
   }

}
