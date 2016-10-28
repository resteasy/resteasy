package org.jboss.resteasy.test.providers.plain;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.CharSetTest;
import org.jboss.resteasy.test.providers.plain.resource.CharsetFoo;
import org.jboss.resteasy.test.providers.plain.resource.CharsetResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Plain provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1066. If the content-type of the response is not specified in the request,
 *
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", str, entity);
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, where the charset is specified
     * by the resource method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStringProducesUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/produces/string/utf16"));
        String str = "La Règle du Jeu";
        logger.info(str);
        Response response = target.request().post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", str, entity);
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, where the charset is specified
     * by the Accept header.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStringAcceptsUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/string/default"));
        String str = "La Règle du Jeu";
        logger.info(str);
        Response response = target.request().accept(WILDCARD_UTF16_TYPE).post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", str, entity);
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", foo.valueOf(), entity);
    }

    /**
     * @tpTestDetails Tests DefaultTextPlain provider, where the charset is specified
     * by the resource method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFooProducesUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/produces/foo/utf16"));
        CharsetFoo foo = new CharsetFoo("La Règle du Jeu");
        logger.info(foo);
        Response response = target.request().post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", foo.valueOf(), entity);
    }

    /**
     * @tpTestDetails Tests DefaultTextPlain provider, where the charset is specified
     * by the Accept header.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFooAcceptsUtf16() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/foo/default"));
        CharsetFoo foo = new CharsetFoo("La Règle du Jeu");
        logger.info(foo);
        Response response = target.request().accept(TEXT_PLAIN_UTF16_TYPE).post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("The response from the server is not what was expected", foo.valueOf(), entity);
    }

    /**
     * @tpTestDetails Tests StringTextStar provider, default charset
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormDefault() throws Exception {
        WebTarget target = client.target(generateURL("/accepts/form/default"));
        Form form  = new Form().param("title", "La Règle du Jeu");
        Response response = target.request().post(Entity.form(form));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not what was expected", "title=La Règle du Jeu", response.readEntity(String.class));
    }
}
