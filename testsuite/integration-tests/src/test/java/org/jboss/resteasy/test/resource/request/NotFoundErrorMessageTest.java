package org.jboss.resteasy.test.resource.request;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import java.io.IOException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.DuplicateDeploymentResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-3725
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NotFoundErrorMessageTest {
    static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
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
    public void testDeploy() throws IOException {
        int initWarningCount = getWarningCount();
        Response response = client.target(generateURL("/nonsence")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
        response.close();

        Assertions.assertEquals(0, getWarningCount() - initWarningCount,
                "Wrong count of warning messages in logs");
    }
}
