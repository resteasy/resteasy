package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.DebugLoggingServerSetup;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingEndPoint;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItem;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItemCorruptedGet;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItemCorruptedSet;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test logging for JsonBinding exceptions
 *                    Regression test for RESTEASY-2106 and RESTEASY-2056.
 * @tpSince RESTEasy 4.0.0.Beta7
 */
@RunWith(Arquillian.class)
@ServerSetup({DebugLoggingServerSetup.class}) // TBD: remove debug logging activation?
public class JsonBindingDebugLoggingTest {

   private static final Logger LOG = Logger.getLogger(JsonBindingDebugLoggingTest.class);
   static ResteasyClient client;

   @Deployment
   public static Archive<?> createTestArchive1() {
      WebArchive war = TestUtil.prepareArchive(JsonBindingDebugLoggingTest.class.getSimpleName());
      war.addClass(JsonBindingDebugLoggingItem.class);
      war.addClass(JsonBindingDebugLoggingItemCorruptedGet.class);
      war.addClass(JsonBindingDebugLoggingItemCorruptedSet.class);
      war.addClasses(LogCounter.class, PortProviderUtil.class, TestUtil.class);
      return TestUtil.finishContainerPrepare(war, null, JsonBindingDebugLoggingEndPoint.class);
   }

   @Before
   public void init() {
      client = (ResteasyClient) ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JsonBindingDebugLoggingTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Check exception during server sending
    * @tpSince RESTEasy 4.0.0.Beta7
    *
    * In this scenario the exception originates in JsonBindingProvider.writeTo.
    * The exception is passed up the call stack to SynchronousDispatcher.writeException.
    * The ExceptionHandler processes the original exception and identifies it as an
    * UnhandledException.  The exception is written to the server.log by this class
    * and the original exception is wrapped in an UnhandledException class and returned
    * up the call stack.  This exception is passed through undertow code which adds
    * HTML formatting to the message that is returned to the caller.
    */
   @Test
   public void exceptionDuringServerSend() throws Exception {

      LogCounter applicationExcpetionLog = new LogCounter(
              "Caused by: java.lang.RuntimeException: "
            + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName(),
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      // perform request
      WebTarget base = client.target(generateURL("/get/nok"));
      Response response = base.request().get();

      // check response
      Assert.assertThat("Wrong response code", response.getStatus(), is(500));
      Assert.assertThat("Response message doesn't contains full stacktrace",
              response.readEntity(String.class),
            containsString("java.lang.RuntimeException: "
                    + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()
      ));

      // checking server.log contents
      // For applicationExcpetionLog and yassonExceptionLog there are
      // always 2 log records for this text.  One is generated from
      // resteasy and one is generated from undertow.
      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(2));
   }


   /**
    * @tpTestDetails Check exception during server receiving
    * @tpSince RESTEasy 4.0.0.Beta7
    *
    * In this scenario the exception originates in JsonBindingProvider.readFrom.
    * The exception is passed up the call stack to MessageBodyParameterInjector.inject
    * which identifies it as read exception and wraps it in a ReaderException class.
    * This exception continues up the call stack to SynchronousDispatcher.writeException.
    * The ExceptionHandler processes it as a ReaderException.  The exception is
    * written to the server.log by this class.  A Response object is created.  The
    * exception message is assigned as the (String) entity in the response object.
    * The response object is returned to the caller.
    */
   @Test
   public void exceptionDuringServerReceive() throws Exception {

      LogCounter applicationExcpetionLog = new LogCounter(
              "Caused by: java.lang.RuntimeException: "
            + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName(), true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      // perform request
      WebTarget base = client.target(generateURL("/post"));
      JsonBindingDebugLoggingItem wrongItem = new JsonBindingDebugLoggingItem();
      wrongItem.setA(5);
      Response response = base.request().post(Entity.entity(wrongItem,
              MediaType.APPLICATION_JSON));

      // check response
      Assert.assertThat("Wrong response code", response.getStatus(), is(400));

      // assert log messages after request in the server.log
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
   }

   /**
    * @tpTestDetails Check exception during server receiving
    * @tpSince RESTEasy 4.0.0.Beta7
    *
    * In this scenario the exception originates in JsonBindingProvider.readFrom.
    * The exception is passed up the call stack to ClientResponse.readEntity which
    * catches it as a RuntimeException.  The original exception is written to the
    * server.log and returned to the caller.
    */
   @Test
   public void exceptionDuringClientReceive() throws Exception {
      // perform request
      WebTarget base = client.target(generateURL("/get/ok"));
      Response response = base.request().get();

      LogCounter applicationExcpetionLog = new LogCounter(
              "Caused by: java.lang.RuntimeException: "
            + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName(),
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);


      // use JsonBindingProvider to get exception
      try {
         response.readEntity(JsonBindingDebugLoggingItemCorruptedSet.class);
         Assert.fail("Client doesn't throw Exception during reading of corrupted data");
      } catch (ProcessingException e) {
         StringWriter errors = new StringWriter();
         e.printStackTrace(new PrintWriter(errors));
         String stackTrace = errors.toString();

         Assert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
               containsString("Caused by: java.lang.RuntimeException: "
                       + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()));
      }

      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
   }

   /**
    * @tpTestDetails Check exception during server sending
    * @tpSince RESTEasy 4.0.0.Beta7
    *
    * In this scenario the exception originates in JsonBindingProvider.writeTo.
    * The exception is passed up the call stack to ApacheHttpClient43Engine.invoke
    * which catches it as an exception.  The original exception is written to the
    * server.log and wrapped in ProcessingException and returned to the caller.
    */
   @Test
   public void exceptionDuringClientSend() throws Exception {

      LogCounter applicationExcpetionLog = new LogCounter(
              "Caused by: java.lang.RuntimeException: "+
              JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName(),
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);


      // use JsonBindingProvider to get exception
      try {
         // perform request
         WebTarget base = client.target(generateURL("/get/ok"));
         base.request().post(Entity.entity(new JsonBindingDebugLoggingItemCorruptedGet(), MediaType.APPLICATION_JSON));
      } catch (ProcessingException e) {
         StringWriter errors = new StringWriter();
         e.printStackTrace(new PrintWriter(errors));
         String stackTrace = errors.toString();

         Assert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
               containsString("Caused by: java.lang.RuntimeException: "
                       + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()));
      }

      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
   }
}
