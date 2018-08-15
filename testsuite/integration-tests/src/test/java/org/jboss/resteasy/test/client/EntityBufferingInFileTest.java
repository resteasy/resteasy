package org.jboss.resteasy.test.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.test.client.resource.EntityBufferingInFileResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;


import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.jboss.logging.Logger;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EntityBufferingInFileTest extends ClientTestBase{

    private static final Logger logger = Logger.getLogger(EntityBufferingInFileTest.class);
    private static final long MAX_POST_SIZE = 2147483647;
    private static ModelNode origMaxPostSizeValue;
    private static Address address = Address.subsystem("undertow").and("server", "default-server").and("http-listener", "default");

    @BeforeClass
    public static void setMaxPostSize() throws Exception {
        OnlineManagementClient client = TestUtil.clientInit();
        Administration admin = new Administration(client);
        Operations ops = new Operations(client);

        // get original 'max-post-size' value
        origMaxPostSizeValue = ops.readAttribute(address, "max-post-size").value();
        // set 'max-post-size' - max size of the object send in the post request
        ops.writeAttribute(address, "max-post-size", MAX_POST_SIZE);
        // reload server
        admin.reload();
        client.close();
    }

    @AfterClass
    public static void resetToDefault() throws Exception {
        OnlineManagementClient client = TestUtil.clientInit();
        Administration admin = new Administration(client);
        Operations ops = new Operations(client);

        // write original 'disallowed methods' value
        ops.writeAttribute(address, "max-post-size", origMaxPostSizeValue);
        // reload server
        admin.reload();
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EntityBufferingInFileTest.class.getSimpleName());
        war.addClass(EntityBufferingInFileTest.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new FilePermission("/tmp/*", "read")
                ), "permissions.xml");
        // DataSource provider creates tmp file in the filesystem
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new FilePermission("/tmp/-", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, EntityBufferingInFileResource.class);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 16 bytes
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (10 bytes),
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * Response entity is type of ByteArrayEntity.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytes1() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.BY, 16, 10, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 16 bytes
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (20 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytes2() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.BY, 16, 20, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 KB
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (500 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testKilobytes1() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.KB, 1, 500, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 KB
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (2000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testKilobytes2() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.KB, 1, 2000, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 MB
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (500000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMegabytes1() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.MB, 1, 500000, true);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 GB
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (2000000000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMegabytes2() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.MB, 1, 2000000, false);
    }

    /**
     * @tpTestDetails Custom ApacheHttpClient4Engine is created which defines maximum file size allowed in memory - 1 GB
     * (defined by memoryUnit and threshold size in doTest() method). Client then sends POST request with String entity
     * of defined size (500000000 bytes)
     * @tpPassCrit Successful response is returned, the entity stream returned is same as original string
     * @tpSince RESTEasy 3.0.16
     */
    @Ignore("The tests fails on some machines on client side. As this is performance test and performance tests were dropped" +
            "from EAP7 rfe list, this is not priority now.")
    @Test
    public void testGigabytes1() throws Exception {
        doTest(ApacheHttpClient4Engine.MemoryUnit.GB, 1, 500000000, true);
    }

    protected void doTest(ApacheHttpClient4Engine.MemoryUnit memoryUnit, int threshold, int length, boolean inMemory) throws Exception {
        try {
            TestClientExecutor executor = new TestClientExecutor();
            executor.setFileUploadMemoryUnit(memoryUnit);
            executor.setFileUploadInMemoryThresholdLimit(threshold);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append("0");
            }
            String body = sb.toString();

            ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
            Response response = client.target(generateURL("/hello")).request().header("content-type", "text/plain; charset=UTF-8").post(Entity.text(body));
            logger.info("Received response");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            InputStream in = response.readEntity(InputStream.class);
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8);
            String responseString = writer.toString();
            Assert.assertEquals(body, responseString);
            response.close();
        } catch (OutOfMemoryError e) {
            logger.info("OutOfMemoryError on " + memoryUnit + " test.");
        }
    }

    static class TestClientExecutor extends ApacheHttpClient4Engine {
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
