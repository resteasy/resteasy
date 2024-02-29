package org.jboss.resteasy.test.providers.jsonb.basic;

import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.test.ContainerConstants;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test logging for JsonBinding exceptions
 *                    Regression test for RESTEASY-2106 and RESTEASY-2056.
 * @tpSince RESTEasy 4.0.0.Beta7
 */
@ExtendWith(ArquillianExtension.class)
@ServerSetup({ LoggingSetupTask.class }) // TBD: remove debug logging activation?
public class JsonBindingDebugLoggingTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive1() {
        WebArchive war = TestUtil.prepareArchive(JsonBindingDebugLoggingTest.class.getSimpleName());
        war.addClass(JsonBindingDebugLoggingItem.class);
        war.addClass(JsonBindingDebugLoggingItemCorruptedGet.class);
        war.addClass(JsonBindingDebugLoggingItemCorruptedSet.class);
        war.addClasses(LogCounter.class, PortProviderUtil.class, TestUtil.class);
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
                new PropertyPermission("quarkus.tester", "read"),
                new PropertyPermission("jboss.server.base.dir", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, JsonBindingDebugLoggingEndPoint.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        LogCounter peStringLog = new LogCounter(".*jakarta.ws.rs.ProcessingException.*",
                false, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

        LogCounter resteasyExceptionLog = new LogCounter(
                ".*ERROR .* RESTEASY002025.*", true,
                ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

        LogCounter jsonbExceptionLog = new LogCounter(
                "Caused by: jakarta.json.bind.JsonbException",
                true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

        // perform request
        WebTarget base = client.target(generateURL("/get/nok"));
        Response response = base.request().get();
        // check response
        Assertions.assertEquals(500, response.getStatus(), "Wrong response code");
        final String body = response.readEntity(String.class);
        Assertions.assertAll("Response message doesn't contains full stacktrace",
                () -> Assertions.assertTrue(body.contains(JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName())),
                () -> Assertions
                        .assertTrue(body.contains("jakarta.json.bind.JsonbException: Unable to serialize property 'a'")),
                () -> Assertions.assertTrue(body.contains("RESTEASY008205")));

        Assertions.assertEquals(0, resteasyExceptionLog.count(), "RESTEasy exception should not be logged");
        Assertions.assertTrue(jsonbExceptionLog.count() > 0, "Jakarta JSON Binding exception should be logged");

        Assertions.assertEquals(1, peStringLog.count(), "There are not only 1 error logs in server");
    }

    /**
     * @tpTestDetails Check exception during server receiving
     * @tpSince RESTEasy 4.0.0.Beta7
     */
    @Test
    public void exceptionDuringServerReceive() throws Exception {
        LogCounter resteasyExceptionLog = new LogCounter(".*ERROR .* RESTEASY002375.*", true,
                ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

        LogCounter jsonbExceptionLog = new LogCounter("Caused by: jakarta.json.bind.JsonbException", true,
                ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
        LogCounter applicationExcpetionLog = new LogCounter(
                "Caused by: java.lang.RuntimeException: "
                        + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName(),
                true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

        // perform request
        WebTarget base = client.target(generateURL("/post"));
        JsonBindingDebugLoggingItem wrongItem = new JsonBindingDebugLoggingItem();
        wrongItem.setA(5);
        Response response = base.request().post(Entity.entity(wrongItem,
                MediaType.APPLICATION_JSON));

        final String body = response.readEntity(String.class);
        Assertions.assertAll("Response message doesn't contains proper message",
                () -> Assertions.assertTrue(body.contains("RESTEASY008200: JSON Binding deserialization error")),
                () -> Assertions.assertTrue(body.contains(JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName())),
                () -> Assertions.assertTrue(body.contains("jakarta.json.bind.JsonbException: ")));

        Assertions.assertEquals(1, applicationExcpetionLog.count(), "Application Exception should be logged");
        Assertions.assertEquals(1, resteasyExceptionLog.count(), "RESTEasy exception should be logged");
        Assertions.assertTrue(jsonbExceptionLog.count() > 0, "Jakarta JSON Binding exception should be logged");
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
                ".*DEBUG .* RESTEASY002340.*", true,
                ContainerConstants.DEFAULT_CONTAINER_QUALIFIER, true);

        LogCounter jsonbExceptionLog = new LogCounter(
                "Caused by: jakarta.json.bind.JsonbException",
                true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
        LogCounter applicationExcpetionLog = new LogCounter(
                "Caused by: java.lang.RuntimeException: "
                        + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName(),
                true, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

        // use JsonBindingProvider to get exception
        try {
            response.readEntity(JsonBindingDebugLoggingItemCorruptedSet.class);
            Assertions.fail("Client doesn't throw Exception during reading of corrupted data");
        } catch (ProcessingException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String stackTrace = errors.toString();

            Assertions.assertTrue(stackTrace.contains("jakarta.json.bind.JsonbException"),
                    "Stracktrace doesn't contain jakarta.json.bind.JsonbException");
            Assertions.assertTrue(stackTrace.contains("Caused by: java.lang.RuntimeException: "
                    + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()),
                    "Stracktrace doesn't contain application exception");
        }

        Assertions.assertEquals(1, applicationExcpetionLog.count(), "Application Exception should be logged");
        Assertions.assertEquals(1, resteasyExceptionLog.count(), "RESTEasy exception should be logged");
        Assertions.assertTrue(jsonbExceptionLog.count() > 0, "Jakarta JSON Binding exception should be logged");
        Assertions.assertEquals(0, errorStringLog.count(), "There shouldn't be any error logs in client");
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

        LogCounter jsonbExceptionLog = new LogCounter(
                "Caused by: jakarta.json.bind.JsonbException", true,
                ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);
        LogCounter applicationExcpetionLog = new LogCounter(
                "Caused by: java.lang.RuntimeException: " +
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

            Assertions.assertTrue(stackTrace.contains("jakarta.json.bind.JsonbException"),
                    "Stracktrace doesn't contain jakarta.json.bind.JsonbException");
            Assertions.assertTrue(stackTrace.contains("Caused by: java.lang.RuntimeException: "
                    + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()),
                    "Stracktrace doesn't contain application exception");
        }

        Assertions.assertEquals(1, applicationExcpetionLog.count(), "Application Exception should be logged");
        Assertions.assertTrue(jsonbExceptionLog.count() > 0, "Jakarta JSON Binding exception should be logged");
        Assertions.assertEquals(1, resteasyExceptionLog.count(), "RESTEasy exception should be logged");
        Assertions.assertEquals(0, errorStringLog.count(), "There shouldn't be any error logs in client");
    }
}
