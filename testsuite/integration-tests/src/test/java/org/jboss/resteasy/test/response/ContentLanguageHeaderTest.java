package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.ContentLanguageHeaderResource;
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
 * @tpSubChapter Resteasy Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check presence of Content-Language header in a response
 * @tpSince RESTEasy 3.8.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ContentLanguageHeaderTest {

    static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ContentLanguageHeaderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ContentLanguageHeaderResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newBuilder().build();
    }

    @AfterAll
    public static void after() {
        client.close();
    }

    /**
     * @tpTestDetails Test for Content-Language header set by ResponseBuilder.language method.
     * @tpSince RESTEasy 3.8.0
     */
    @Test
    public void testLanguage() {
        Response response = client
                .target(PortProviderUtil.generateURL("/language", ContentLanguageHeaderTest.class.getSimpleName())).request()
                .get();
        MultivaluedMap<String, Object> headers = response.getHeaders();

        Assertions.assertTrue(headers.keySet().contains("Content-Language"),
                "Content-Language header is not present in response");
        Assertions.assertEquals("en-us", headers.getFirst("Content-Language"),
                "Content-Language header does not have expected value");
    }

    /**
     * @tpTestDetails Test for Content-Language header set as Variant by Response.ok method.
     * @tpSince RESTEasy 3.8.0
     */
    @Test
    public void testLanguageOk() {
        Response response = client
                .target(PortProviderUtil.generateURL("/language-ok", ContentLanguageHeaderTest.class.getSimpleName())).request()
                .get();
        MultivaluedMap<String, Object> headers = response.getHeaders();

        Assertions.assertTrue(headers.keySet().contains("Content-Language"),
                "Content-Language header is not present in response");
        Assertions.assertTrue("en-us".equalsIgnoreCase(headers.getFirst("Content-Language").toString()),
                "Content-Language header does not have expected value");
    }

    /**
     * @tpTestDetails Test for Content-Language header set as Variant by ResponseBuilder.variant method.
     * @tpSince RESTEasy 3.8.0
     */
    @Test
    public void testLanguageVariant() {
        Response response = client
                .target(PortProviderUtil.generateURL("/language-variant", ContentLanguageHeaderTest.class.getSimpleName()))
                .request().get();
        MultivaluedMap<String, Object> headers = response.getHeaders();

        Assertions.assertTrue(headers.keySet().contains("Content-Language"),
                "Content-Language header is not present in response");
        Assertions.assertTrue("en-us".equalsIgnoreCase(headers.getFirst("Content-Language").toString()),
                "Content-Language header does not have expected value");
    }

}
