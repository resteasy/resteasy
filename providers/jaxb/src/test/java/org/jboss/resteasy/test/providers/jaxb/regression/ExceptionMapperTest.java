package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-519
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperTest extends BaseResourceTest
{
   @Path("/test")
   public static class TestService
   {
      @POST
      @Consumes("application/xml")
      public void post(Person person)
      {

      }
   }

   @Override
   @Before
   public void before() throws Exception
   {
      addExceptionMapper(JAXBMapper.class);
      addPerRequestResource(TestService.class, Person.class);
      super.before();
   }

   @Provider
   public static class JAXBMapper implements ExceptionMapper<JAXBUnmarshalException>
   {
      @Override
      public Response toResponse(JAXBUnmarshalException exception)
      {
         return Response.status(400).type("text/plain").entity(exception.getMessage()).build();
      }
   }

   @Test
   public void testFailure() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test"));
      request.body("application/xml", "<person");
      ClientResponse<?> response = request.post();
      Assert.assertEquals(400, response.getStatus());
      String output = response.getEntity(String.class);
      System.out.println(output);
   }


}
