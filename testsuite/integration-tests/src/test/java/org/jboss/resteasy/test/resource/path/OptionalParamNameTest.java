package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.path.resource.OptionalParamNameResource;
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
 * Test for optional parameter names feature (GitHub issue #579)
 * Tests that @PathParam, @QueryParam, @MatrixParam, @FormParam, @HeaderParam, and @CookieParam
 * can infer parameter names from method parameter names when annotation value is not specified.
 *
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test optional parameter name inference for Jakarta REST parameter annotations
 * @tpSince RESTEasy 7.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class OptionalParamNameTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(OptionalParamNameTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, OptionalParamNameResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, OptionalParamNameTest.class.getSimpleName());
    }

    /**
     * Test @PathParam with inferred parameter name
     */
    @Test
    public void testPathParamInferred() {
        Response response = client.target(generateURL("/path/John/Doe"))
                .request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Hello, John Doe", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @PathParam with explicit parameter name (backward compatibility)
     */
    @Test
    public void testPathParamExplicit() {
        Response response = client.target(generateURL("/path-explicit/Jane/Smith"))
                .request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Hello, Jane Smith", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @QueryParam with inferred parameter name
     */
    @Test
    public void testQueryParamInferred() {
        Response response = client.target(generateURL("/query"))
                .queryParam("search", "test")
                .queryParam("page", "1")
                .request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("search=test, page=1", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @HeaderParam with inferred parameter name
     */
    @Test
    public void testHeaderParamInferred() {
        Response response = client.target(generateURL("/header"))
                .request()
                .header("authorization", "Bearer token123")
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Auth: Bearer token123", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @MatrixParam with inferred parameter name
     */
    @Test
    public void testMatrixParamInferred() {
        Response response = client.target(generateURL("/matrix/segment;color=red;size=large"))
                .request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("segment, color=red, size=large", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @CookieParam with inferred parameter name
     */
    @Test
    public void testCookieParamInferred() {
        Response response = client.target(generateURL("/cookie"))
                .request()
                .cookie("sessionId", "abc123")
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Session: abc123", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test @FormParam with inferred parameter name
     */
    @Test
    public void testFormParamInferred() {
        Response response = client.target(generateURL("/form"))
                .request()
                .post(jakarta.ws.rs.client.Entity.form(
                        new jakarta.ws.rs.core.Form()
                                .param("username", "testuser")
                                .param("password", "testpass")));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("User: testuser", response.readEntity(String.class));
        response.close();
    }

    /**
     * Test mixed usage - some explicit, some inferred
     */
    @Test
    public void testMixedUsage() {
        Response response = client.target(generateURL("/mixed/user123/item456"))
                .queryParam("sort", "name")
                .queryParam("order", "asc")
                .request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("userId=user123, itemId=item456, sort=name, order=asc",
                response.readEntity(String.class));
        response.close();
    }
}

// Made with Bob
