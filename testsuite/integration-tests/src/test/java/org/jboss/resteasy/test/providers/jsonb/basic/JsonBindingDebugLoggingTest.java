package org.jboss.resteasy.test.providers.jsonb.basic;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

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

import org.hamcrest.MatcherAssert;
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

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test logging for JsonBinding exceptions
 *                    Regression test for RESTEASY-2106 and RESTEASY-2056.
 * @tpSince RESTEasy 4.0.0.Beta7
 */
@RunWith(Arquillian.class)
@ServerSetup({ DebugLoggingServerSetup.class }) // TBD: remove debug logging activation?
@Category(NotForBootableJar.class)
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
        MatcherAssert.assertThat("Wrong response code", response.getStatus(), is(500));
        MatcherAssert.assertThat("Response message doesn't contains full stacktrace",
                response.readEntity(String.class), allOf(
                        containsString(JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()),
                        containsString("jakarta.json.bind.JsonbException: Unable to serialize property 'a'"),
                        containsString("RESTEASY008205")));

        MatcherAssert.assertThat("RESTEasy exception should be logged",
                resteasyExceptionLog.count(), is(0));
        MatcherAssert.assertThat("Jakarta JSON Binding exception should be logged",
                jsonbExceptionLog.count(), greaterThan(0));

        MatcherAssert.assertThat("There are not only 1 error logs in server",
                peStringLog.count(), is(1));
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

        // check response
        MatcherAssert.assertThat("Response message doesn't contains proper message",
                response.readEntity(String.class), allOf(
                        containsString("RESTEASY008200: JSON Binding deserialization error"),
                        containsString(JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()),
                        containsString("jakarta.json.bind.JsonbException: ")));

        // assert log messages after request
        MatcherAssert.assertThat("Application Exception should be logged",
                applicationExcpetionLog.count(), is(1));
        MatcherAssert.assertThat("RESTEasy exception should be logged",
                resteasyExceptionLog.count(), is(1));
        MatcherAssert.assertThat("Jakarta JSON Binding exception should be logged",
                jsonbExceptionLog.count(), greaterThanOrEqualTo(1));
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
            Assert.fail("Client doesn't throw Exception during reading of corrupted data");
        } catch (ProcessingException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String stackTrace = errors.toString();

            MatcherAssert.assertThat("Stracktrace doesn't contain jakarta.json.bind.JsonbException", stackTrace,
                    containsString("jakarta.json.bind.JsonbException"));
            MatcherAssert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
                    containsString("Caused by: java.lang.RuntimeException: "
                            + JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName()));
        }

        // assert log messages after request
        MatcherAssert.assertThat("Application Exception should be logged",
                applicationExcpetionLog.count(), is(1));
        MatcherAssert.assertThat("RESTEasy exception should be logged",
                resteasyExceptionLog.count(), is(1));
        MatcherAssert.assertThat("Jakarta JSON Binding exception should be logged",
                jsonbExceptionLog.count(), greaterThanOrEqualTo(1));
        MatcherAssert.assertThat("There shouldn't be any error logs in client",
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

            MatcherAssert.assertThat("Stracktrace doesn't contain jakarta.json.bind.JsonbException", stackTrace,
                    containsString("jakarta.json.bind.JsonbException"));
            MatcherAssert.assertThat("Stracktrace doesn't contain application exception", stackTrace,
                    containsString("Caused by: java.lang.RuntimeException: "
                            + JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName()));
        }

        // assert log messages after request
        MatcherAssert.assertThat("Application Exception should be logged",
                applicationExcpetionLog.count(), is(1));
        MatcherAssert.assertThat("Jakarta JSON Binding exception should be logged",
                jsonbExceptionLog.count(), greaterThan(0));
        MatcherAssert.assertThat("RESTEasy exception should be logged",
                resteasyExceptionLog.count(), is(1));
        MatcherAssert.assertThat("There shouldn't be any error logs in client",
                errorStringLog.count(), is(0));
    }
}
