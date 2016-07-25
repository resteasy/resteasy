package org.jboss.resteasy.test.providers.jaxb;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.KeepCharsetFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.providers.jaxb.resource.KeepCharsetMovieResource;
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
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1066. If the content-type of the response is not specified in the request,
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class KeepCharsetTest {

    protected final Logger logger = Logger.getLogger(KeepCharsetTest.class.getName());
    static ResteasyClient client;
    protected static final MediaType APPLICATION_XML_UTF16_TYPE;

    private static final String EXPAND = "war_expand";
    private static final String NO_EXPAND = "war_no_expand";
    private static final String entityXml = "<?xml version=\"1.0\"?>\r"
            + "<keepCharsetFavoriteMovieXmlRootElement><title>La Règle du Jeu</title></keepCharsetFavoriteMovieXmlRootElement>";

    static {
        Map<String, String> params = new HashMap<String, String>();
        params.put("charset", "UTF-16");
        APPLICATION_XML_UTF16_TYPE = new MediaType("application", "xml", params);
    }

    @Deployment(name = NO_EXPAND)
    public static Archive<?> deployExpandFalse() {
        Map<String, String> params = new HashMap<String, String>();
        WebArchive war = TestUtil.prepareArchive(NO_EXPAND);
        params.put("charset", "UTF-16");
        params.put("resteasy.document.expand.entity.references", "false");
        return TestUtil.finishContainerPrepare(war, params, KeepCharsetMovieResource.class, KeepCharsetFavoriteMovieXmlRootElement.class);
    }

    @Deployment(name = EXPAND)
    public static Archive<?> deployExpandTrue() {
        Map<String, String> params = new HashMap<String, String>();
        WebArchive war = TestUtil.prepareArchive(EXPAND);
        params.put("charset", "UTF-16");
        params.put("resteasy.document.expand.entity.references", "true");
        return TestUtil.finishContainerPrepare(war, params, KeepCharsetMovieResource.class, KeepCharsetFavoriteMovieXmlRootElement.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Default encoding is used and entity expansion is set to true.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlDefaultExpand() throws Exception {
        xmlDefault(EXPAND);
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Default encoding is used and entity expansion is set to false.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlDefaultNoExpand() throws Exception {
        xmlDefault(NO_EXPAND);
    }

    private void xmlDefault(String path) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xml/default", path));
        logger.info(entityXml);
        logger.info("client default charset: " + Charset.defaultCharset());
        logger.info("Sending request");
        Response response = target.request().accept(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(entityXml, MediaType.APPLICATION_XML_TYPE));
        logger.info("Received response");
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        KeepCharsetFavoriteMovieXmlRootElement entity = response.readEntity(KeepCharsetFavoriteMovieXmlRootElement.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("Incorrect xml entity was returned from the server", "La Règle du Jeu", entity.getTitle());
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Encoding is set up in resource produces annotation and entity expansion is set to true.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlProducesExpand() throws Exception {
        XmlProduces(EXPAND);
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Encoding is set up in resource produces annotation and entity expansion is set to false.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlProducesNoExpand() throws Exception {
        XmlProduces(NO_EXPAND);
    }

    private void XmlProduces(String path) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xml/produces", path));
        logger.info(entityXml);
        logger.info("client default charset: " + Charset.defaultCharset());
        Response response = target.request().post(Entity.entity(entityXml, APPLICATION_XML_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        KeepCharsetFavoriteMovieXmlRootElement entity = response.readEntity(KeepCharsetFavoriteMovieXmlRootElement.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("Incorrect xml entity was returned from the server", "La Règle du Jeu", entity.getTitle());
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Encoding is set up in the request accepts header and entity expansion is set to true.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlAcceptsExpand() throws Exception {
        XmlAccepts(EXPAND);
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity. Request encoding is different than from
     * encoding of the server. Encoding is set up in the request accepts header and entity expansion is set to false.
     * @tpPassCrit The response is returned in encoding of the original request not in encoding of the server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlAcceptsNoExpand() throws Exception {
        XmlAccepts(NO_EXPAND);
    }

    private void XmlAccepts(String path) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xml/accepts", path));
        logger.info(entityXml);
        logger.info("client default charset: " + Charset.defaultCharset());
        Response response = target.request().accept(APPLICATION_XML_UTF16_TYPE).post(Entity.entity(entityXml, APPLICATION_XML_UTF16_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        KeepCharsetFavoriteMovieXmlRootElement entity = response.readEntity(KeepCharsetFavoriteMovieXmlRootElement.class);
        logger.info("Result: " + entity);
        Assert.assertEquals("Incorrect xml entity was returned from the server", "La Règle du Jeu", entity.getTitle());
    }
}
