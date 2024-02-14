package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateAsProviderHeader;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateAsProviderHeaderDelegate;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateAsProviderResource;
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
 * @tpSubChapter HeaderDelegates discovered via @Provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2059
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HeaderDelegateAsProviderTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(HeaderDelegateAsProviderTest.class.getSimpleName());
        war.addClass(HeaderDelegateAsProviderHeader.class);
        war.addClass(HeaderDelegateAsProviderHeaderDelegate.class);
        war.addAsResource(HeaderDelegateAsProviderTest.class.getPackage(),
                "jakarta.ws.rs.ext.Providers_HeaderDelegateAsProvider",
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, HeaderDelegateAsProviderResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
        client.register(HeaderDelegateAsProviderHeaderDelegate.class);
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HeaderDelegateAsProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Verify HeaderDelegate is discovered and used sending header from server
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeaderDelegateServer() {
        Response response = client.target(generateURL("/server")).request().get();
        Assertions.assertEquals("toString:abc;xyz", response.getHeaderString("HeaderTest"));
    }

    /**
     * @tpTestDetails Verify HeaderDelegate is discovered and used sending header from client, injected as @HeaderParam
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeaderDelegateClientHeader() {
        Builder request = client.target(generateURL("/client/header")).request();
        String response = request.header("HeaderTest", new HeaderDelegateAsProviderHeader("123", "789")).get(String.class);
        Assertions.assertEquals("fromString:toString:123|789", response);
    }

    /**
     * @tpTestDetails Verify HeaderDelegate is discovered and used sending header from client, injected as @Context HttpHeaders
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeaderDelegateClientHeaders() {
        Builder request = client.target(generateURL("/client/headers")).request();
        String response = request.header("HeaderTest", new HeaderDelegateAsProviderHeader("123", "789")).get(String.class);
        Assertions.assertEquals("toString:123;789", response);
    }
}
