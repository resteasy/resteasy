package org.jboss.resteasy.test.resource.request;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.core.basic.resource.DuplicateDeploymentResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-3725
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NotFoundErrorMessageTest {
    static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    private static int getWarningCount() {
        return TestUtil.getWarningCount("RESTEASY002010", false, DEFAULT_CONTAINER_QUALIFIER);
    }

    @Deployment
    public static Archive<?> deploy() {

        WebArchive war = TestUtil.prepareArchive(NotFoundErrorMessageTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, DuplicateDeploymentResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NotFoundErrorMessageTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check that no ERROR message was in logs after 404.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testDeploy() throws IOException {
        int initWarningCount = getWarningCount();
        Response response = client.target(generateURL("/nonsence")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
        response.close();

        Assert.assertEquals("Wrong count of warning messages in logs", 0, getWarningCount() - initWarningCount);
    }
}