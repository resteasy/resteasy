package org.jboss.resteasy.test.response;

import java.math.BigDecimal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.ProduceConsumeData;
import org.jboss.resteasy.test.response.resource.ProduceConsumeResource;
import org.jboss.resteasy.test.response.resource.ProduceConsumeTextData;
import org.jboss.resteasy.test.response.resource.ProduceConsumeWildData;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProduceConsumeTest {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProduceConsumeTest.class.getSimpleName());
        war.addClass(ProduceConsumeData.class);
        return TestUtil.finishContainerPrepare(war, null, ProduceConsumeResource.class, ProduceConsumeWildData.class,
                ProduceConsumeTextData.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProduceConsumeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request, server return empty successful response. Client parses the response
     *                and tries to read it as BigDecimal.class object.
     * @tpPassCrit Instance of NoContentException is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmpty() {
        Response response = client.target(generateURL("/resource/empty")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_PLAIN_TYPE);
        try {
            BigDecimal big = response.readEntity(BigDecimal.class);
            Assertions.fail();
        } catch (ProcessingException e) {
            Assertions.assertTrue(e.getCause() instanceof NoContentException);
        }

    }

    /**
     * @tpTestDetails Client sends GET request, server return empty successful response. Client parses the response
     *                and tries to read it as Character.class object.
     * @tpPassCrit Instance of NoContentException is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyCharacter() {
        Response response = client.target(generateURL("/resource/empty")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_PLAIN_TYPE);
        try {
            Character big = response.readEntity(Character.class);
            Assertions.fail();
        } catch (ProcessingException e) {
            Assertions.assertTrue(e.getCause() instanceof NoContentException);
        }

    }

    /**
     * @tpTestDetails Client sends GET request, server return empty successful response. Client parses the response
     *                and tries to read it as Integer.class object.
     * @tpPassCrit Instance of NoContentException is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyInteger() {
        Response response = client.target(generateURL("/resource/empty")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_PLAIN_TYPE);
        try {
            Integer big = response.readEntity(Integer.class);
            Assertions.fail();
        } catch (ProcessingException e) {
            Assertions.assertTrue(e.getCause() instanceof NoContentException);
        }

    }

    /**
     * @tpTestDetails Client sends GET request, server return empty successful response. Client parses the response
     *                and tries to read it as MultivaluedMap.class object.
     * @tpPassCrit The returned MultivaluedMap object is null
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyForm() {
        Response response = client.target(generateURL("/resource/empty")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_FORM_URLENCODED);
        MultivaluedMap big = response.readEntity(MultivaluedMap.class);

        Assertions.assertTrue(big == null || big.size() == 0);
    }

    /**
     * @tpTestDetails Client sends POST request with entity of mediatype WILDCARD. The application has two providers to
     *                write and read Data object. One for mediatype text/plain and one wildcard provider. The server choses one
     *                provider
     *                and sends response back to the client
     * @tpPassCrit The text/plain provider is chosen by the server, because if the request has wildcard mediatype,
     *             the most specific provider has to be chosen.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWild() {
        client.register(ProduceConsumeTextData.class);
        client.register(ProduceConsumeWildData.class);
        Response response = client.target(generateURL("/resource/wild")).request("*/*")
                .post(Entity.entity("data", MediaType.WILDCARD_TYPE));
        Assertions.assertEquals(response.getStatus(), 200);
        ProduceConsumeData data = response.readEntity(ProduceConsumeData.class);
        Assertions.assertEquals("Data{data='data:text:text', type='text'}", data.toString());
        response.close();
    }

}
