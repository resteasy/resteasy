package org.jboss.resteasy.test.resource.request;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.request.resource.DateFormatPathResource;
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
 * @tpTestCaseDetails Tests date encoding as query parameter
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class DateFormatPathTest {
    static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DateFormatPathTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, DateFormatPathResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DateFormatPathTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test date 08/26/2009
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDate() throws Exception {
        Response response = client.target(generateURL("/widget/08%2F26%2F2009")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("08/26/2009", response.readEntity(String.class));
        response.close();
    }
}
