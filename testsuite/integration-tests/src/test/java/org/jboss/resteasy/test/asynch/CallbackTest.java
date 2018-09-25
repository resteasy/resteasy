package org.jboss.resteasy.test.asynch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.asynch.resource.CallbackResource;
import org.jboss.resteasy.test.asynch.resource.CallbackExceptionThrowingStringBean;
import org.jboss.resteasy.test.asynch.resource.CallbackResourceBase;
import org.jboss.resteasy.test.asynch.resource.CallbackSecondSettingCompletionCallback;
import org.jboss.resteasy.test.asynch.resource.CallbackStringBean;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletAsyncResponseBlockingQueue;
import org.jboss.resteasy.test.asynch.resource.CallbackTimeoutHandler;
import org.jboss.resteasy.test.asynch.resource.CallbackSettingCompletionCallback;
import org.jboss.resteasy.test.asynch.resource.CallbackStringBeanEntityProvider;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for async exception handling
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CallbackTest {
   public static Client client;

   @BeforeClass
   public static void initClient() {
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();
   }

   @AfterClass
   public static void closeClient() {
      client.close();
   }

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(CallbackTest.class.getSimpleName());
      war.addClasses(CallbackResource.class,
            CallbackExceptionThrowingStringBean.class,
            CallbackTimeoutHandler.class,
            CallbackResourceBase.class,
            CallbackSecondSettingCompletionCallback.class,
            CallbackSettingCompletionCallback.class,
            CallbackStringBean.class,
            CallbackStringBeanEntityProvider.class,
            JaxrsAsyncServletAsyncResponseBlockingQueue.class);
      war.addAsWebInfResource(AsyncPostProcessingTest.class.getPackage(), "CallbackTestWeb.xml", "web.xml");
      return TestUtil.finishContainerPrepare(war, null, CallbackResource.class, CallbackStringBeanEntityProvider.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, CallbackTest.class.getSimpleName());
   }

   protected void invokeClear() {
      Response response = client.target(generateURL("/resource/clear")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
      response.close();
   }

   protected void invokeReset() {
      Response response = client.target(generateURL("/resource/reset")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
      response.close();
   }

   protected void assertString(Future<Response> future, String check) throws Exception {
      Response response = future.get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      String entity = response.readEntity(String.class);
      Assert.assertEquals(entity, check);

   }

   /**
    * @tpTestDetails Argument contains exception in two callback classes
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void argumentContainsExceptionInTwoCallbackClassesTest() throws Exception {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target(generateURL("/resource/suspend")).request().async().get();

      Future<Response> register = client.target(generateURL("/resource/registerclasses?stage=0")).request().async().get();
      assertString(register, CallbackResourceBase.FALSE);

      Future<Response> exception = client.target(generateURL("/resource/exception?stage=1")).request().async().get();
      Response response = exception.get();
      Assert.assertEquals("Request return wrong response", CallbackResourceBase.TRUE, response.readEntity(String.class));

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target(generateURL("/resource/error")).request().async().get();
      assertString(error, RuntimeException.class.getName());
      error = client.target(generateURL("/resource/seconderror")).request().async().get();
      assertString(error, RuntimeException.class.getName());
   }

   /**
    * @tpTestDetails Argument contains exception in two callback instances
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void argumentContainsExceptionInTwoCallbackInstancesTest() throws Exception {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target(generateURL("/resource/suspend")).request().async().get();

      Future<Response> register = client.target(generateURL("/resource/registerobjects?stage=0")).request().async().get();
      assertString(register, CallbackResourceBase.FALSE);

      Future<Response> exception = client.target(generateURL("/resource/exception?stage=1")).request().async().get();
      Response response = exception.get();
      Assert.assertEquals("Request return wrong response", CallbackResourceBase.TRUE, response.readEntity(String.class));

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target(generateURL("/resource/error")).request().async().get();
      assertString(error, RuntimeException.class.getName());
      error = client.target(generateURL("/resource/seconderror")).request().async().get();
      assertString(error, RuntimeException.class.getName());
   }

   /**
    * @tpTestDetails Argument contains exception when sending IO exception
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void argumentContainsExceptionWhenSendingIoExceptionTest() throws Exception {
      invokeClear();
      invokeReset();
      Future<Response> suspend = client.target(generateURL("/resource/suspend")).request().async().get();

      Future<Response> register = client.target(generateURL("/resource/register?stage=0")).request().async().get();
      assertString(register, CallbackResourceBase.FALSE);

      Future<Response> exception = client.target(generateURL("/resource/resumechecked?stage=1")).request().async().get();
      Response response = exception.get();
      Assert.assertEquals("Request return wrong response", CallbackResourceBase.TRUE, response.readEntity(String.class));

      Response suspendResponse = suspend.get();
      Assert.assertEquals(suspendResponse.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
      suspendResponse.close();

      Future<Response> error = client.target(generateURL("/resource/error")).request().async().get();
      assertString(error, IOException.class.getName());
   }
}
