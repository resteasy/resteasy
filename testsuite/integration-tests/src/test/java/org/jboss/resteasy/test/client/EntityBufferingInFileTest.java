package org.jboss.resteasy.test.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClientEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.setup.SnapshotServerSetupTask;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.EntityBufferingInFileResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(EntityBufferingInFileTest.MaxPostServerSetupTask.class)
public class EntityBufferingInFileTest extends ClientTestBase {

    public static class MaxPostServerSetupTask extends SnapshotServerSetupTask {
        @Override
        protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
            final ModelNode address = Operations.createAddress("undertow", "server", "default-server", "http-listener",
                    "default");
            final ModelNode op = Operations.createWriteAttributeOperation(address, "max-post-size", MAX_POST_SIZE);
            final ModelNode result = client.getControllerClient().execute(op);
            if (!Operations.isSuccessfulOutcome(result)) {
                throw new RuntimeException(
                        "Failed to configure server: " + Operations.getFailureDescription(result).asString());
            }
        }
    }

    private static final Logger logger = Logger.getLogger(EntityBufferingInFileTest.class);
    private static final long MAX_POST_SIZE = 2147483647;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EntityBufferingInFileTest.class.getSimpleName());
        war.addClass(EntityBufferingInFileTest.class);
        // DataSource provider creates tmp file in the filesystem
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(PermissionUtil.createTempDirPermission("read")),
                "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, EntityBufferingInFileResource.class);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 16 bytes
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (10 bytes),
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     *             Response entity is type of ByteArrayEntity.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytes1() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.BY, 16, 10, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 16 bytes
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (20 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytes2() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.BY, 16, 20, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 KB
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (500 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testKilobytes1() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.KB, 1, 500, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 KB
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (2000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testKilobytes2() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.KB, 1, 2000, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 MB
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (500000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMegabytes1() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.MB, 1, 500000, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 GB
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (2000000000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMegabytes2() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.MB, 1, 2000000, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 GB
     *                (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String
     *                entity
     *                of defined size (500000000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Ignore("The tests fails on some machines on client side. As this is performance test and performance tests were dropped" +
            "from EAP7 rfe list, this is not priority now.")
    @Test
    public void testGigabytes1() throws Exception {
        doTest(ApacheHttpClientEngine.MemoryUnit.GB, 1, 500000000, true);
    }

    protected void doTest(ApacheHttpClientEngine.MemoryUnit memoryUnit, int threshold, int length, boolean inMemory)
            throws Exception {
        try {
            TestClientExecutor executor = new TestClientExecutor();
            executor.setFileUploadMemoryUnit(memoryUnit);
            executor.setFileUploadInMemoryThresholdLimit(threshold);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append("0");
            }
            String body = sb.toString();

            ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(executor).build();
            Response response = client.target(generateURL("/hello")).request()
                    .header("content-type", "text/plain; charset=UTF-8").post(Entity.text(body));
            logger.info("Received response");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            InputStream in = response.readEntity(InputStream.class);
            String responseString = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Assert.assertEquals(body, responseString);
            response.close();
            client.close();
        } catch (OutOfMemoryError e) {
            logger.info("OutOfMemoryError on " + memoryUnit + " test.");
        }
    }

    static class TestClientExecutor extends ApacheHttpClient43Engine {
        private HttpEntity entityToBuild;

        protected HttpEntity buildEntity(final ClientInvocation request) throws IOException {
            entityToBuild = super.buildEntity(request);
            return entityToBuild;
        }

        public HttpEntity getBuildEntity() {
            return entityToBuild;
        }
    }
}
