package org.jboss.resteasy.test.async;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.async.callback.Resource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CallbackTest
{
   public static Client client;

   @BeforeClass
   public static void initClient()
   {
      client = new ResteasyClientBuilder().connectionPoolSize(10).build();
   }

   @AfterClass
   public static void closeClient()
   {
      client.close();
   }

   protected void invokeClear()
   {
      Response response = client.target("http://localhost:8080/resource/clear").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();
   }

   protected void invokeReset()
   {
      Response response = client.target("http://localhost:8080/resource/reset").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();
   }

   protected void assertString(Future<Response> future, String check) throws Exception
   {
      Response response = future.get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      Assert.assertEquals(entity, check);

   }


   @Test
   public void argumentContainsExceptionInTwoCallbackClassesTest() throws Exception
   {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target("http://localhost:8080/resource/suspend").request().async().get();

      Future<Response> register = client.target("http://localhost:8080/resource/registerclasses?stage=0").request().async().get();
      assertString(register, Resource.FALSE);

      Future<Response> exception = client.target("http://localhost:8080/resource/exception?stage=1").request().async().get();
      Response response = exception.get();

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target("http://localhost:8080/resource/error").request().async().get();
      assertString(error, RuntimeException.class.getName());
       error = client.target("http://localhost:8080/resource/seconderror").request().async().get();
      assertString(error, RuntimeException.class.getName());
   }

   @Test
   public void argumentContainsExceptionInTwoCallbackInstancesTest() throws Exception
   {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target("http://localhost:8080/resource/suspend").request().async().get();

      Future<Response> register = client.target("http://localhost:8080/resource/registerobjects?stage=0").request().async().get();
      assertString(register, Resource.FALSE);

      Future<Response> exception = client.target("http://localhost:8080/resource/exception?stage=1").request().async().get();
      Response response = exception.get();

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target("http://localhost:8080/resource/error").request().async().get();
      assertString(error, RuntimeException.class.getName());
      error = client.target("http://localhost:8080/resource/seconderror").request().async().get();
      assertString(error, RuntimeException.class.getName());
   }

   @Test
   public void argumentContainsExceptionWhenSendingIoExceptionTest() throws Exception
   {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target("http://localhost:8080/resource/suspend").request().async().get();

      Future<Response> register = client.target("http://localhost:8080/resource/register?stage=0").request().async().get();
      assertString(register, Resource.FALSE);

      Future<Response> exception = client.target("http://localhost:8080/resource/resumechecked?stage=1").request().async().get();
      Response response = exception.get();

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target("http://localhost:8080/resource/error").request().async().get();
      assertString(error, IOException.class.getName());
   }





}
