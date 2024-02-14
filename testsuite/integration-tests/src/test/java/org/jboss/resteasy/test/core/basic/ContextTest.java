package org.jboss.resteasy.test.core.basic;

import java.io.FilePermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.ContextAfterEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextBeforeEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextEndInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextService;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-699
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ContextTest {
    public static final String WRONG_RESPONSE_ERROR_MSG = "Wrong content of response";

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, ContextTest.class.getSimpleName() + ".war");
        war.addClasses(ContextAfterEncoderInterceptor.class, ContextBeforeEncoderInterceptor.class, ContextService.class,
                ContextEncoderInterceptor.class, ContextEndInterceptor.class);
        war.addAsWebInfResource(ContextTest.class.getPackage(), "ContextIndex.txt", "index.txt");
        war.addAsWebInfResource(ContextTest.class.getPackage(), "ContextWeb.xml", "web.xml");
        // undertow requires read permission in order to perform forward request.
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new FilePermission("<<ALL FILES>>", "read")), "permissions.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test for forwarding request to external HTML file
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testForward() throws Exception {
        Response response = client.target(generateURL("/test/forward")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello world", response.readEntity(String.class),
                "Wrong content of response");
        response.close();
    }

    /**
     * @tpTestDetails Base URL should not be affected by URL parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRepeat() throws Exception {
        Response response = client.target(generateURL("/test/test")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(generateURL("/test/"), response.readEntity(String.class),
                "Resource get wrong injected URL");
        response.close();
        response = client.target(generateURL("/test/")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(generateURL("/test/"), response.readEntity(String.class),
                "Resource get wrong injected URL");
        response.close();
    }

    /**
     * @tpTestDetails Test for getting servlet context in REST resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testServletContext() throws Exception {
        final String HEADER_ERROR_MESSAGE = "Response don't have correct headers";
        Response response = client.target(generateURL("/test/test/servletcontext")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ok", response.readEntity(String.class), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertNotNull(response.getHeaderString("before-encoder"), HEADER_ERROR_MESSAGE);
        Assertions.assertNotNull(response.getHeaderString("after-encoder"), HEADER_ERROR_MESSAGE);
        Assertions.assertNotNull(response.getHeaderString("end"), HEADER_ERROR_MESSAGE);
        Assertions.assertNotNull(response.getHeaderString("encoder"), HEADER_ERROR_MESSAGE);
        response.close();
    }

    /**
     * @tpTestDetails Test for getting servlet config in REST resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testServletConfig() throws Exception {
        Response response = client.target(generateURL("/test/test/servletconfig")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ok", response.readEntity(String.class), WRONG_RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails XML extension mapping test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlMappings() throws Exception {
        Response response = client.target(generateURL("/test/stuff.xml")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("xml", response.readEntity(String.class), WRONG_RESPONSE_ERROR_MSG);
        response.close();

    }

    /**
     * @tpTestDetails Json extension mapping test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJsonMappings() throws Exception {
        Response response = client.target(generateURL("/test/stuff.json")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("json", response.readEntity(String.class), WRONG_RESPONSE_ERROR_MSG);
        response.close();
    }
}
