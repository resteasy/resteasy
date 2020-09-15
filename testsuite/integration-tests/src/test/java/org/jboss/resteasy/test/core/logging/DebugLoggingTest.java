package org.jboss.resteasy.test.core.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingEndPoint;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingReaderInterceptorCustom;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingWriterInterceptorCustom;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test debug messages for used Interceptors and Providers.
 *                    Regression test for RESTEASY-1415 and RESTEASY-1558.
 * @tpSince RESTEasy 3.0.20.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({
        NotForBootableJar.class  // no log check support for bootable-jar in RESTEasy TS so far
})
public class DebugLoggingTest {

   static ResteasyClient client;
   protected static final Logger logger = LogManager.getLogger(DebugLoggingTest.class.getName());

   private static final String BUILD_IN = "build-in";
   private static final String CUSTOM = "custom";

   @Deployment(name = BUILD_IN, order = 1)
   public static Archive<?> createTestArchive1() {
      WebArchive war = TestUtil.prepareArchive(BUILD_IN);
      return TestUtil.finishContainerPrepare(war, null, DebugLoggingEndPoint.class);
   }

   @Deployment(name = CUSTOM, order = 2)
   public static Archive<?> createTestArchive2() {
      WebArchive war = TestUtil.prepareArchive(CUSTOM);
      return TestUtil.finishContainerPrepare(war, null, DebugLoggingEndPoint.class, DebugLoggingReaderInterceptorCustom.class,
            DebugLoggingWriterInterceptorCustom.class, DebugLoggingCustomReaderAndWriter.class);
   }

   @BeforeClass
   public static void initLogging() throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();

      // enable RESTEasy debug logging
      TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=ALL)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:add(level=ALL)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=javax.xml.bind:add(level=ALL)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=com.fasterxml.jackson:add(level=ALL)");

      client.close();
   }

   @AfterClass
   public static void removeLogging() throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();

      // enable RESTEasy debug logging
      TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=INFO)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:remove()");
      TestUtil.runCmd(client, "/subsystem=logging/logger=javax.xml.bind:remove()");
      TestUtil.runCmd(client, "/subsystem=logging/logger=com.fasterxml.jackson:remove()");

      client.close();
   }

   @Before
   public void init() {
      client = new ResteasyClientBuilder().build();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Check build-in providers and interceptors
    * @tpSince RESTEasy 3.0.20.Final
    */
   @Test
   public void  testBuildIn() throws Exception {
      // count log messages before request
      LogCounter bodyReaderStringLog = new LogCounter("MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter bodyWriterStringLog = new LogCounter("MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter readerInterceptorLog = new LogCounter("ReaderInterceptor: org.jboss.resteasy.security.doseta.DigitalVerificationInterceptor", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter writerInterceptorLog = new LogCounter("WriterInterceptor: org.jboss.resteasy.security.doseta.DigitalSigningInterceptor", false, DEFAULT_CONTAINER_QUALIFIER);

      // perform request
      WebTarget base = client.target(PortProviderUtil.generateURL("/build/in", BUILD_IN));
      Response response = base.request().post(Entity.text("data"));
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      String strResponse = response.readEntity(String.class);
      Assert.assertEquals("Wrong response", "data", strResponse);

      // assert log messages after request
      Assert.assertThat("Correct body reader was not used/logged", bodyReaderStringLog.count(), greaterThan(0));
      Assert.assertThat("Correct body writer was not used/logged", bodyWriterStringLog.count(), greaterThan(0));
      Assert.assertThat("Correct reader interceptor was not used/logged", readerInterceptorLog.count(), greaterThan(0));
      Assert.assertThat("Correct writer interceptor was not used/logged", writerInterceptorLog.count(), greaterThan(0));
   }

   /**
    * @tpTestDetails Check user's custom providers and interceptors
    * @tpSince RESTEasy 3.0.20.Final
    */
   @Test
   public void  testCustom() throws Exception {
      // count log messages before request
      LogCounter bodyReaderStringLog = new LogCounter("MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter bodyWriterStringLog = new LogCounter("MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter readerInterceptorLog = new LogCounter("ReaderInterceptor: org.jboss.resteasy.test.core.logging.resource.DebugLoggingReaderInterceptorCustom", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter writerInterceptorLog = new LogCounter("WriterInterceptor: org.jboss.resteasy.test.core.logging.resource.DebugLoggingWriterInterceptorCustom", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter bodyReaderCustomLog = new LogCounter("MessageBodyReader: org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter", false, DEFAULT_CONTAINER_QUALIFIER);
      LogCounter bodyWriterCustomLog = new LogCounter("MessageBodyWriter: org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter", false, DEFAULT_CONTAINER_QUALIFIER);

      // perform request
      TestUtil.getWarningCount("MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      TestUtil.getWarningCount("MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
      WebTarget base = client.target(PortProviderUtil.generateURL("/custom", CUSTOM));
      Response response = base.request().post(Entity.entity("data", "aaa/bbb"));
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      String strResponse = response.readEntity(String.class);
      Assert.assertEquals("Wrong response", "wi_datadata", strResponse);

      // assert log messages after request
      Assert.assertThat("Incorrect body reader was used/logged", bodyReaderStringLog.count(), is(0));
      Assert.assertThat("Incorrect body writer was used/logged", bodyWriterStringLog.count(), is(0));
      Assert.assertThat("Correct readerInterceptor was not used/logged", readerInterceptorLog.count(), greaterThan(0));
      Assert.assertThat("Correct writerInterceptor was not used/logged", writerInterceptorLog.count(), greaterThan(0));
      Assert.assertThat("Correct body reader was not used/logged", bodyReaderCustomLog.count(), greaterThan(0));
      Assert.assertThat("Correct body writer was not used/logged", bodyWriterCustomLog.count(), greaterThan(0));
   }

}
