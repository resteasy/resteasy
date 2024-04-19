package org.jboss.resteasy.test.core.logging;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingEndPoint;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingReaderInterceptorCustom;
import org.jboss.resteasy.test.core.logging.resource.DebugLoggingWriterInterceptorCustom;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test debug messages for used Interceptors and Providers.
 *                    Regression test for RESTEASY-1415 and RESTEASY-1558.
 * @tpSince RESTEasy 3.1.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(DebugLoggingTest.DebugLoggingSetupTask.class)
public class DebugLoggingTest {

    public static class DebugLoggingSetupTask extends LoggingSetupTask {

        public DebugLoggingSetupTask() {
            super(Map.of("ALL", Set.of("org.jboss.resteasy", "jakarta.xml.bind", "com.fasterxml.jackson")));
        }
    }

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(DebugLoggingTest.class.getName());

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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check build-in providers and interceptors
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testBuildIn() throws Exception {
        // count log messages before request
        LogCounter bodyReaderStringLog = new LogCounter(
                "MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
        LogCounter bodyWriterStringLog = new LogCounter(
                "MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
        LogCounter readerInterceptorLog = new LogCounter(
                "ReaderInterceptor: org.jboss.resteasy.security.doseta.DigitalVerificationInterceptor", false,
                DEFAULT_CONTAINER_QUALIFIER);
        LogCounter writerInterceptorLog = new LogCounter(
                "WriterInterceptor: org.jboss.resteasy.security.doseta.DigitalSigningInterceptor", false,
                DEFAULT_CONTAINER_QUALIFIER);

        // perform request
        WebTarget base = client.target(PortProviderUtil.generateURL("/build/in", BUILD_IN));
        Response response = base.request().post(Entity.text("data"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String strResponse = response.readEntity(String.class);
        Assertions.assertEquals("data", strResponse, "Wrong response");

        // assert log messages after request
        Assertions.assertTrue(bodyReaderStringLog.count() > 0,
                "Correct body reader was not used/logged");
        Assertions.assertTrue(bodyWriterStringLog.count() > 0,
                "Correct body writer was not used/logged");
        Assertions.assertTrue(readerInterceptorLog.count() > 0,
                "Correct reader interceptor was not used/logged");
        Assertions.assertTrue(writerInterceptorLog.count() > 0,
                "Correct writer interceptor was not used/logged");
    }

    /**
     * @tpTestDetails Check user's custom providers and interceptors
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testCustom() throws Exception {
        // count log messages before request
        LogCounter bodyReaderStringLog = new LogCounter(
                "MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
        LogCounter bodyWriterStringLog = new LogCounter(
                "MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false, DEFAULT_CONTAINER_QUALIFIER);
        LogCounter readerInterceptorLog = new LogCounter(
                "ReaderInterceptor: org.jboss.resteasy.test.core.logging.resource.DebugLoggingReaderInterceptorCustom", false,
                DEFAULT_CONTAINER_QUALIFIER);
        LogCounter writerInterceptorLog = new LogCounter(
                "WriterInterceptor: org.jboss.resteasy.test.core.logging.resource.DebugLoggingWriterInterceptorCustom", false,
                DEFAULT_CONTAINER_QUALIFIER);
        LogCounter bodyReaderCustomLog = new LogCounter(
                "MessageBodyReader: org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter", false,
                DEFAULT_CONTAINER_QUALIFIER);
        LogCounter bodyWriterCustomLog = new LogCounter(
                "MessageBodyWriter: org.jboss.resteasy.test.core.logging.resource.DebugLoggingCustomReaderAndWriter", false,
                DEFAULT_CONTAINER_QUALIFIER);

        // perform request
        TestUtil.getWarningCount("MessageBodyReader: org.jboss.resteasy.plugins.providers.StringTextStar", false,
                DEFAULT_CONTAINER_QUALIFIER);
        TestUtil.getWarningCount("MessageBodyWriter: org.jboss.resteasy.plugins.providers.StringTextStar", false,
                DEFAULT_CONTAINER_QUALIFIER);
        WebTarget base = client.target(PortProviderUtil.generateURL("/custom", CUSTOM));
        Response response = base.request().post(Entity.entity("data", "aaa/bbb"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String strResponse = response.readEntity(String.class);
        Assertions.assertEquals("wi_datadata", strResponse, "Wrong response");

        // assert log messages after request
        Assertions.assertEquals(bodyReaderStringLog.count(), 0,
                "Incorrect body reader was used/logged");
        Assertions.assertEquals(bodyWriterStringLog.count(), 0,
                "Incorrect body writer was used/logged");
        Assertions.assertTrue(readerInterceptorLog.count() > 0,
                "Correct readerInterceptor was not used/logged");
        Assertions.assertTrue(writerInterceptorLog.count() > 0,
                "Correct writerInterceptor was not used/logged");
        Assertions.assertTrue(bodyReaderCustomLog.count() > 0,
                "Correct body reader was not used/logged");
        Assertions.assertTrue(bodyWriterCustomLog.count() > 0,
                "Correct body writer was not used/logged");
    }

}
