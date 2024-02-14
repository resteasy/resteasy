package org.jboss.resteasy.test.asynch;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.LoggingPermission;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.asynch.resource.AsynchBasicResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic asynchronous test for "resteasy.async.job.service.max.job.results" property.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class AsynchBasicTest {
    private static org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(AsynchBasicTest.class);

    public static CountDownLatch latch;

    private static final String DEFAULT_DEPLOYMENT = "AsynchBasicTestBasic";
    private static final String ONE_MAX_DEPLOYMENT = "AsynchBasicTestOne";
    private static final String TEN_MAX_DEPLOYMENT = "AsynchBasicTestTen";

    public static Archive<?> deploy(String deploymentName, String maxSize) {
        WebArchive war = TestUtil.prepareArchive(deploymentName);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");

        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.async.job.service.enabled", "true");
        if (maxSize != null) {
            contextParam.put("resteasy.async.job.service.max.job.results", maxSize);
        }
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, contextParam, AsynchBasicResource.class);
    }

    @Deployment(name = DEFAULT_DEPLOYMENT)
    public static Archive<?> deployDefault() {
        return deploy(DEFAULT_DEPLOYMENT, null);
    }

    @Deployment(name = ONE_MAX_DEPLOYMENT)
    public static Archive<?> deployOne() {
        return deploy(ONE_MAX_DEPLOYMENT, "1");
    }

    @Deployment(name = TEN_MAX_DEPLOYMENT)
    public static Archive<?> deployTen() {
        return deploy(TEN_MAX_DEPLOYMENT, "10");
    }

    private ResteasyClient initClient() {
        return ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    /**
     * @tpTestDetails Test oneway=true query parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(DEFAULT_DEPLOYMENT)
    public void testOneway() throws Exception {
        ResteasyClient client = initClient();
        Response response = null;
        try {
            latch = new CountDownLatch(1);
            long start = System.currentTimeMillis();
            response = client.target(generateURL("?oneway=true", DEFAULT_DEPLOYMENT)).request()
                    .put(Entity.entity("content", "text/plain"));

            //response = request.put();
            long end = System.currentTimeMillis() - start;
            Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
            Assertions.assertTrue(end < 1000);
            Assertions.assertTrue(latch.await(2, TimeUnit.SECONDS),
                    "Request was not sent correctly");
        } finally {
            response.close();
            client.close();
        }
    }

    /**
     * @tpTestDetails Use default value of resteasy.async.job.service.max.job.results
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(DEFAULT_DEPLOYMENT)
    public void testAsynchBasic() throws Exception {
        final int MAX = 4;
        ResteasyClient client = initClient();

        latch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        Response response = client.target(generateURL("?asynch=true", DEFAULT_DEPLOYMENT)).request()
                .post(Entity.entity("content", "text/plain"));
        @SuppressWarnings("unused")
        long end = System.currentTimeMillis() - start;
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
        response.close();

        response = client.target(jobUrl).request().get();
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS),
                "Request was not sent correctly");
        response.close();

        // there's a lag between when the latch completes and the executor
        // registers the completion of the call
        for (int i = 0; i <= MAX; i++) {
            response = client.target(jobUrl).request().get();
            Thread.sleep(1000);
            if (HttpServletResponse.SC_OK == response.getStatus()) {
                Assertions.assertEquals("content", response.readEntity(String.class),
                        "Wrong response content");
                response.close();
                break;
            }
            response.close();
            if (i == MAX) {
                Assertions.fail("Expected response with status code 200");
            }
        }

        // test its still there
        response = client.target(jobUrl).request().get();
        Thread.sleep(1000);
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("content", response.readEntity(String.class),
                "Wrong response content");
        response.close();

        // delete and test delete
        response = client.target(jobUrl).request().delete();
        Assertions.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.close();

        response = client.target(jobUrl).request().get();
        Thread.sleep(1000);
        Assertions.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
        response.close();

        client.close();
    }

    /**
     * @tpTestDetails Set value of resteasy.async.job.service.max.job.results to 1. Try to store to cache to items.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(ONE_MAX_DEPLOYMENT)
    public void testAsynchOne() throws Exception {
        ResteasyClient client = initClient();

        // test cache size
        latch = new CountDownLatch(1);
        Response response = client.target(generateURL("?asynch=true", ONE_MAX_DEPLOYMENT)).request()
                .post(Entity.entity("content", "text/plain"));
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl1 = response.getHeaderString(HttpHeaders.LOCATION);
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS), "Request was not sent correctly");
        response.close();

        latch = new CountDownLatch(1);
        response = client.target(generateURL("?asynch=true", ONE_MAX_DEPLOYMENT)).request()
                .post(Entity.entity("content", "text/plain"));
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS), "Request was not sent correctly");
        Assertions.assertTrue(!jobUrl1.equals(jobUrl2), "There are only one response for two requests");
        response.close();

        response = client.target(jobUrl1).request().get();
        Thread.sleep(1000);
        Assertions.assertEquals(HttpServletResponse.SC_GONE,
                response.getStatus(), "Response should be gone, but server still remember it");
        response.close();

        // test its still there
        response = client.target(jobUrl2).request().get();
        Thread.sleep(1000);
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("content", response.readEntity(String.class),
                "Wrong content of response");
        response.close();

        // delete and test delete
        response = client.target(jobUrl2).request().delete();
        Assertions.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.close();
        response = client.target(jobUrl2).request().get();
        Assertions.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
        response.close();

        client.close();
    }

    /**
     * @tpTestDetails Test default value of resteasy.server.cache.maxsize. It should be 100. Try to store 110 items to cache. 10
     *                items should be gone.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(DEFAULT_DEPLOYMENT)
    public void testAsynchMaxSizeDefault() throws Exception {
        ResteasyClient client = initClient();

        ArrayList<String> jobs = new ArrayList<>();
        for (int i = 0; i < 110; i++) {
            // test cache size
            latch = new CountDownLatch(1);
            Response response = client.target(generateURL("?asynch=true", DEFAULT_DEPLOYMENT)).request()
                    .post(Entity.entity("content", "text/plain"));
            Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
            String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
            logger.info(i + ": " + jobUrl);
            jobs.add(jobUrl);
            response.close();
            Thread.sleep(50);
        }

        Thread.sleep(2000);

        for (int i = 0; i < 10; i++) {
            Response response = client.target(jobs.get(i)).request().get();
            logger.info(
                    i + " (" + jobs.get(i) + "): get " + response.getStatus() + ", expected: " + HttpServletResponse.SC_GONE);
            Assertions.assertEquals(HttpServletResponse.SC_GONE,
                    response.getStatus(), "Response should be gone, but server still remember it");
            response.close();
            Thread.sleep(50);
        }

        for (int i = 10; i < 110; i++) {
            Response response = client.target(jobs.get(i)).request().get();
            logger.info(i + " (" + jobs.get(i) + "): get " + response.getStatus() + ", expected: " + HttpServletResponse.SC_OK);
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("content", response.readEntity(String.class),
                    "Wrong content of response");
            response.close();
            Thread.sleep(50);
        }

        client.close();
    }

    /**
     * @tpTestDetails Set value of resteasy.server.cache.maxsize to 10. Try to restore item from cache.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(TEN_MAX_DEPLOYMENT)
    public void testAsynchTen() throws Exception {
        ResteasyClient client = initClient();

        // test readAndRemove
        latch = new CountDownLatch(1);
        Response response = client.target(generateURL("?asynch=true", TEN_MAX_DEPLOYMENT)).request()
                .post(Entity.entity("content", "text/plain"));
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS), "Request was not sent correctly");
        response.close();
        Thread.sleep(50);

        // test its still there
        response = client.target(jobUrl2).request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("content", response.readEntity(String.class),
                "Wrong content of response");
        response.close();
        Thread.sleep(50);

        response = client.target(jobUrl2).request().get();
        Thread.sleep(1000);
        Assertions.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
        response.close();

        client.close();
    }

}
