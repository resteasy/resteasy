package org.jboss.resteasy.test.core.encoding;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.encoding.resource.SpecialCharactersProxy;
import org.jboss.resteasy.test.core.encoding.resource.SpecialCharactersResource;
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
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-208 and RESTEASY-214
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SpecialCharactersTest {

    protected static ResteasyClient client;

    @BeforeEach
    public void setup() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void shutdown() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SpecialCharactersTest.class.getSimpleName());
        war.addClass(SpecialCharactersProxy.class);
        return TestUtil.finishContainerPrepare(war, null, SpecialCharactersResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SpecialCharactersTest.class.getSimpleName());
    }

    private static final String SPACES_REQUEST = "something something";
    private static final String QUERY = "select p from VirtualMachineEntity p where guest.guestId = :id";

    @Test
    public void testEcho() {
        SpecialCharactersProxy proxy = client.target(generateURL("")).proxy(SpecialCharactersProxy.class);
        Assertions.assertEquals(SPACES_REQUEST, proxy.sayHi(SPACES_REQUEST));
        Assertions.assertEquals(QUERY, proxy.compile(QUERY));
    }

    @Test
    public void testIt() throws Exception {
        Response response = client.target(generateURL("/sayhello/widget/08%2F26%2F2009")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("08/26/2009", response.readEntity(String.class),
                "Wrong content of response");
        response.close();
    }

    @Test
    public void testPlus() throws Exception {
        Response response = client.target(generateURL("/sayhello/plus/foo+bar")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        // assert is in resource
        response.close();
    }

    @Test
    public void testPlus2() throws Exception {
        Response response = client.target(generateURL("/sayhello/plus/foo+bar")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        // assert is in resource
        response.close();
    }
}
