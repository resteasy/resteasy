package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.basic.resource.ResponseCommittedResource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1238
 * @tpSince RESTEasy 3.1.3.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResponseCommittedTest {
    public static int TEST_STATUS = 444;
    private static Client client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(ResponseCommittedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResponseCommittedResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ResponseCommittedTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Test
    public void testWorks() throws Exception {
        Invocation.Builder request = client.target(generateBaseUrl()).request();
        Response response = request.get();
        Assertions.assertEquals(TEST_STATUS, response.getStatus());
        response.close();
        client.close();
    }
}
