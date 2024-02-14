package org.jboss.resteasy.test.providers.plain;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.CharSetTest;
import org.jboss.resteasy.test.providers.plain.resource.CharsetFoo;
import org.jboss.resteasy.test.providers.plain.resource.CharsetResource;
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
 * @tpSubChapter Plain provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1066. If the content-type of the response is not specified in the request,
 *
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CharsetTest {

    protected static final Logger logger = Logger.getLogger(CharSetTest.class.getName());
    static ResteasyClient client;
    protected static final MediaType TEXT_PLAIN_UTF16_TYPE;
    protected static final MediaType WILDCARD_UTF16_TYPE;
    private static Map<String, String> params;

    static {
        params = new HashMap<String, String>();
        params.put("charset", "UTF-16");
        TEXT_PLAIN_UTF16_TYPE = new MediaType("text", "plain", params);
        WILDCARD_UTF16_TYPE = new MediaType("*", "*", params);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CharSetTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, params, CharsetResource.class, CharsetFoo.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CharSetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, where the charset is unspecified.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStringDefault() throws Exception {
        logger.info("client default charset: " + Charset.defaultCharset());
        WebTarget target = client.target(generateURL("/accepts/string/default"));
        String str = "La Règle du Jeu";
        logger.info(str);
        Response response = target.request().post(Entity.entity(str, MediaType.WILDCARD_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(str, entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, where the charset is specified
     *                by the resource method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStringProducesUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/produces/string/utf16"));
        String str = "La Règle du Jeu";
        logger.info(str);
        Response response = target.request().post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(str, entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, where the charset is specified
     *                by the Accept header.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStringAcceptsUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/string/default"));
        String str = "La Règle du Jeu";
        logger.info(str);
        Response response = target.request().accept(WILDCARD_UTF16_TYPE).post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(str, entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests DefaultTextPlain provider, where the charset is unspecified.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFooDefault() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/foo/default"));
        CharsetFoo foo = new CharsetFoo("La Règle du Jeu");
        logger.info(foo);
        Response response = target.request().post(Entity.entity(foo, MediaType.TEXT_PLAIN_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(foo.valueOf(), entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests DefaultTextPlain provider, where the charset is specified
     *                by the resource method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFooProducesUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/produces/foo/utf16"));
        CharsetFoo foo = new CharsetFoo("La Règle du Jeu");
        logger.info(foo);
        Response response = target.request().post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(foo.valueOf(), entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests DefaultTextPlain provider, where the charset is specified
     *                by the Accept header.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFooAcceptsUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/foo/default"));
        CharsetFoo foo = new CharsetFoo("La Règle du Jeu");
        logger.info(foo);
        Response response = target.request().accept(TEXT_PLAIN_UTF16_TYPE).post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertEquals(foo.valueOf(), entity,
                "The response from the server is not what was expected");
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, default charset
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormDefault() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/form/default"));
        Form form = new Form().param("title", "La Règle du Jeu");
        Response response = target.request().post(Entity.form(form));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("title=La Règle du Jeu", response.readEntity(String.class),
                "The response from the server is not what was expected");
    }
}
