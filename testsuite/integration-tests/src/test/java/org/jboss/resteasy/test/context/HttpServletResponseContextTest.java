package org.jboss.resteasy.test.context;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.context.resource.HttpServletResponseContextResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * @tpSubChapter Jaxrs implementation
 * @tpChapter Integration tests
 * @tpTestCaseDetails RESTEASY-1531
 * @tpSince RESTEasy 4.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HttpServletResponseContextTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HttpServletResponseContextTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, HttpServletResponseContextResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HttpServletResponseContextTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintString() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/string")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertEquals("context", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintBoolean() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/boolean")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertEquals("true", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintChar() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/char")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertEquals("c", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintInt() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/int")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertEquals("17", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintLong() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/long")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class).toUpperCase();
        Assertions.assertEquals("17", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintFloat() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/float")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class).toUpperCase();
        Assertions.assertEquals("17.0", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintDouble() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/print/double")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class).toUpperCase();
        Assertions.assertEquals("17.0", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnEol() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/eol")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertTrue(s.length() > 0);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnString() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/string")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertTrue(s.startsWith("context") && s.length() > "context".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnBoolean() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/boolean")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertTrue(s.startsWith("true") && s.length() > "true".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    @RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha1")
    public void testPrintlnChar() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/char")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertEquals("c\r\n", s);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnInt() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/int")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertTrue(s.startsWith("17") && s.length() > "17".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnLong() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/long")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class);
        Assertions.assertTrue(s.startsWith("17") && s.length() > "17".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnFloat() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/float")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class).toUpperCase();
        Assertions.assertTrue(s.startsWith("17") && s.length() > "17".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testPrintlnDouble() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/println/double")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        String s = response.readEntity(String.class).toUpperCase();
        Assertions.assertTrue(s.startsWith("17") && s.length() > "17".length());
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testWriteArray1() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/write/array/1")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("context".length(), response.readEntity(byte[].class).length);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testWriteArray3() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/write/array/3")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("context".length() - 1, response.readEntity(byte[].class).length);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testWriteInt() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/write/int")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("A", response.readEntity(String.class));
    }

    /**
     * Tests that the injected response writes to the output stream of the current request, when the same resource
     * method is invoked more than once.
     *
     * @since 8.0.0
     */
    @Test
    public void sameMethodInvokedTwice() {
        try (Client client = ClientBuilder.newClient()) {
            for (int i = 0; i < 2; i++) {
                try (Response response = client.target(generateURL("/write/array/1")).request().get()) {
                    Assertions.assertEquals(200, response.getStatus());
                    Assertions.assertEquals("context", response.readEntity(String.class));
                }
            }
        }
    }

    /**
     * Tests that whether the output stream was written to is tracked for each request: a request which does not write
     * gets a 204 even after a previous request to the same method wrote content.
     *
     * @since 8.0.0
     */
    @Test
    @RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha1")
    public void writtenTrackedPerRequest() {
        try (Client client = ClientBuilder.newClient()) {
            try (Response response = client.target(generateURL("/write/optional")).queryParam("write", true).request()
                    .get()) {
                Assertions.assertEquals(200, response.getStatus());
                Assertions.assertEquals("context", response.readEntity(String.class));
            }
            try (Response response = client.target(generateURL("/write/optional")).queryParam("write", false).request()
                    .get()) {
                Assertions.assertEquals(204, response.getStatus());
            }
        }
    }
}
