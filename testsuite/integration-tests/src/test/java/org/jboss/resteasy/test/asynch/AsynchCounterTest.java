package org.jboss.resteasy.test.asynch;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.asynch.resource.AsynchCounterResource;
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
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests use of SecureRandom to generate location job ids, RESTEASY-1483
 * @tpSince RESTEasy 3.1.0.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsynchCounterTest {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AsynchCounterTest.class.getSimpleName());
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.async.job.service.enabled", "true");
        contextParam.put("resteasy.secure.random.max.use", "2");
        return TestUtil.finishContainerPrepare(war, contextParam, AsynchCounterResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsynchCounterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test that job ids are no longer consecutive
     * @tpInfo RESTEASY-1483
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testAsynchCounter() throws Exception {

        Response response = client.target(generateURL("?asynch=true")).request().get();
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
        int job1 = Integer.parseInt(jobUrl.substring(jobUrl.lastIndexOf('-') + 1));
        response.close();
        response = client.target(generateURL("?asynch=true")).request().get();
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
        int job2 = Integer.parseInt(jobUrl.substring(jobUrl.lastIndexOf('-') + 1));
        Assertions.assertTrue(job2 != job1 + 1);
        response.close();
    }
}
