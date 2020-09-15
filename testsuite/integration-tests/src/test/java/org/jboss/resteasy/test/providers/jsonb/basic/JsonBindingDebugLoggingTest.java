package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.DebugLoggingServerSetup;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingEndPoint;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItem;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItemCorruptedGet;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingDebugLoggingItemCorruptedSet;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test logging for JsonBinding exceptions
 *                    Regression test for RESTEASY-2106 and RESTEASY-2056.
 * @tpSince RESTEasy 4.0.0.Beta7
 */
@RunWith(Arquillian.class)
@ServerSetup({DebugLoggingServerSetup.class}) // TBD: remove debug logging activation?
@Category(NotForBootableJar.class) // no log check support for bootable-jar in RESTEasy TS so far
public class JsonBindingDebugLoggingTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> createTestArchive1() {
      WebArchive war = TestUtil.prepareArchive(JsonBindingDebugLoggingTest.class.getSimpleName());
      war.addClass(JsonBindingDebugLoggingItem.class);
      war.addClass(JsonBindingDebugLoggingItemCorruptedGet.class);
      war.addClass(JsonBindingDebugLoggingItemCorruptedSet.class);
      war.addClasses(LogCounter.class, PortProviderUtil.class, TestUtil.class);
      war.addClass(NotForBootableJar.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new ReflectPermission("suppressAccessChecks"),
              new RuntimePermission("accessDeclaredMembers"),
              new PropertyPermission("arquillian.debug", "read"),
              new PropertyPermission("user.dir", "read"),
              new FilePermission("<<ALL FILES>>", "read"), // required to read jbossas-managed/log/server.log file
              new PropertyPermission("node", "read"),
              new PropertyPermission("ipv6", "read"),
              new RuntimePermission("getenv.RESTEASY_PORT"),
              new PropertyPermission("org.jboss.resteasy.port", "read"),
              new PropertyPermission("jboss.server.base.dir", "read")
      ), "permissions.xml");
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
    */
   @Test
   public void exceptionDuringServerSend() throws Exception {
      // count log messages before request
      LogCounter errorStringLog = new LogCounter("ERROR",
              false, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      LogCounter resteasyExceptionLog = new LogCounter(
              ".*ERROR .* RESTEASY002025.*", true,
              ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

      LogCounter yassonExceptionLog = new LogCounter(
              "Caused by: javax.json.bind.JsonbException",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
      LogCounter yassonStacktraceLog = new LogCounter(
              "at org.eclipse.yasson", true,
              ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

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
              response.readEntity(String.class), allOf(
            containsString("org.eclipse.yasson.internal"),
            containsString("java.lang.RuntimeException: "
                              + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()),
            containsString("javax.json.bind.JsonbException: Error getting value on"),
            containsString("RESTEASY008205")
      ));

      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
      Assert.assertThat("RESTEasy exception should be logged",
              resteasyExceptionLog.count(), is(0));
      Assert.assertThat("Yasson exception should be logged",
              yassonExceptionLog.count(), greaterThan(0));
      Assert.assertThat("Yasson exception stacktrace should be logged",
              yassonStacktraceLog.count(), greaterThan(0));

      Assert.assertThat("There are not only 2 error logs in server",
              errorStringLog.count(), is(1));
   }


   /**
    * @tpTestDetails Check exception during server receiving
    * @tpSince RESTEasy 4.0.0.Beta7
    */
   @Test
   public void exceptionDuringServerReceive() throws Exception {

      // count log messages before request
      LogCounter errorStringLog = new LogCounter("ERROR", true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      LogCounter resteasyExceptionLog = new LogCounter(".*DEBUG .* RESTEASY002305.*", true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

      LogCounter yassonExceptionLog = new LogCounter("Caused by: javax.json.bind.JsonbException", true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
      LogCounter yassonStacktraceLog = new LogCounter("at org.eclipse.yasson", true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
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
      Assert.assertThat("Response message doesn't contains proper message",
              response.readEntity(String.class), allOf(
                      containsString("RESTEASY008200: JSON Binding deserialization error"),
                      containsString(JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()),
                      containsString("javax.json.bind.JsonbException: ")
              ));

      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
      Assert.assertThat("RESTEasy exception should be logged",
              resteasyExceptionLog.count(), is(1));
      Assert.assertThat("Yasson exception should be logged",
              yassonExceptionLog.count(), is(1));
      Assert.assertThat("Yasson exception stacktrace should be logged",
              yassonStacktraceLog.count(), greaterThan(0));

      Assert.assertThat("There shouldn't be any error logs in server",
              errorStringLog.count(), is(0));
   }

   /**
    * @tpTestDetails Check exception during client receiving
    * @tpSince RESTEasy 4.0.0.Beta7
    */
   @Test
   public void exceptionDuringClientReceive() throws Exception {
      // perform request
      WebTarget base = client.target(generateURL("/get/ok"));
      Response response = base.request().get();

      // count log messages before request
      LogCounter errorStringLog = new LogCounter("ERROR",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      LogCounter resteasyExceptionLog = new LogCounter(
              ".*DEBUG .* RESTEASY008200.*", true,
              ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);


      LogCounter yassonExceptionLog = new LogCounter(
              "Caused by: javax.json.bind.JsonbException",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
      LogCounter yassonStacktraceLog = new LogCounter("at org.eclipse.yasson",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
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

         Assert.assertThat("Stracktrace doesn't contain javax.json.bind.JsonbException", stackTrace,
               containsString("javax.json.bind.JsonbException"));
         Assert.assertThat("Stracktrace doesn't contain yasson part", stackTrace,
               containsString("org.eclipse.yasson.internal"));
         Assert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
               containsString("Caused by: java.lang.RuntimeException: "
                       + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()));
      }

      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
      Assert.assertThat("RESTEasy exception should be logged",
         resteasyExceptionLog.count(), is(1));
      Assert.assertThat("Yasson exception should be logged",
              yassonExceptionLog.count(), is(1));
      Assert.assertThat("Yasson exception stacktrace should be logged",
              yassonStacktraceLog.count(), greaterThan(0));
      Assert.assertThat("There shouldn't be any error logs in client",
         errorStringLog.count(), is(0));
   }

   /**
    * @tpTestDetails Check exception during client sending
    * @tpSince RESTEasy 4.0.0.Beta7
    */
   @Test
   public void exceptionDuringClientSend() throws Exception {

      // count log messages before request
      LogCounter errorStringLog = new LogCounter("ERROR",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

      LogCounter resteasyExceptionLog = new LogCounter(
              ".*DEBUG .* RESTEASY004672.*", true,
              ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

      LogCounter yassonExceptionLog = new LogCounter(
              "Caused by: javax.json.bind.JsonbException", true,
              ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
      LogCounter yassonStacktraceLog = new LogCounter("at org.eclipse.yasson",
              true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
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

         Assert.assertThat("Stracktrace doesn't contain javax.json.bind.JsonbException", stackTrace,
               containsString("javax.json.bind.JsonbException"));
         Assert.assertThat("Stracktrace doesn't contain yasson part", stackTrace,
               containsString("org.eclipse.yasson.internal"));
         Assert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
               containsString("Caused by: java.lang.RuntimeException: "
                       + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()));
      }

      // assert log messages after request
      Assert.assertThat("Application Exception should be logged",
              applicationExcpetionLog.count(), is(1));
      Assert.assertThat("Yasson exception should be logged",
              yassonExceptionLog.count(), greaterThan(0));
      Assert.assertThat("RESTEasy exception should be logged",
         resteasyExceptionLog.count(), is(1));
      Assert.assertThat("Yasson exception stacktrace should be logged",
              yassonStacktraceLog.count(), greaterThan(0));
      Assert.assertThat("There shouldn't be any error logs in client",
         errorStringLog.count(), is(0));
   }
}
