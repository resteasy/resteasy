package org.jboss.resteasy.test.providers.jaxb;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.CharSetFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.providers.jaxb.resource.CharSetMovieResource;
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
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1066.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CharSetRE1066Test {
    public static final MediaType APPLICATION_XML_UTF16_TYPE;
    public static final MediaType TEXT_PLAIN_UTF16_TYPE;
    public static final MediaType WILDCARD_UTF16_TYPE;
    public static final String APPLICATION_XML_UTF16 = "application/xml;charset=UTF-16";
    public static final String TEXT_PLAIN_UTF16 = "text/plain;charset=UTF-16";
    public static final String WILDCARD_UTF16 = "*/*;charset=UTF-16";

    private final Logger log = Logger.getLogger(CharSetRE1066Test.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CharSetRE1066Test.class.getSimpleName());
        war.addClass(CharSetRE1066Test.class);
        return TestUtil.finishContainerPrepare(war, null, CharSetMovieResource.class, CharSetFavoriteMovieXmlRootElement.class);
    }

    @BeforeEach
    public void before() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CharSetRE1066Test.class.getSimpleName());
    }

    static {
        Map<String, String> params = new HashMap<String, String>();
        params.put("charset", "UTF-16");
        APPLICATION_XML_UTF16_TYPE = new MediaType("application", "xml", params);
        TEXT_PLAIN_UTF16_TYPE = new MediaType("text", "plain", params);
        WILDCARD_UTF16_TYPE = new MediaType("*", "*", params);
    }

    @Test
    public void testXmlDefault() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/xml/default"));
        Builder request = target.request();
        request.accept(MediaType.APPLICATION_XML_TYPE);

        String str = "<?xml version=\"1.0\"?>\r"
                + "<charSetFavoriteMovieXmlRootElement><title>La Règle du Jeu</title></charSetFavoriteMovieXmlRootElement>";
        log.info(str);
        log.info("client default charset: " + Charset.defaultCharset());
        log.info("Sending request");

        Response response = request.post(Entity.entity(str, MediaType.APPLICATION_XML_TYPE));
        log.info("Received response");

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        CharSetFavoriteMovieXmlRootElement entity = response.readEntity(CharSetFavoriteMovieXmlRootElement.class);
        log.info("Result: " + entity);
        log.info("title: " + entity.getTitle());
        Assertions.assertEquals("La Règle du Jeu", entity.getTitle());
    }

    @Test
    public void testXmlProduces() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/xml/produces"));
        Builder request = target.request();

        String str = "<?xml version=\"1.0\"?>\r"
                + "<charSetFavoriteMovieXmlRootElement><title>La Règle du Jeu</title></charSetFavoriteMovieXmlRootElement>";
        log.info(str);
        log.info("client default charset: " + Charset.defaultCharset());

        Response response = request.post(Entity.entity(str, APPLICATION_XML_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        CharSetFavoriteMovieXmlRootElement entity = response.readEntity(CharSetFavoriteMovieXmlRootElement.class);
        log.info("Result: " + entity);
        log.info("title: " + entity.getTitle());
        Assertions.assertEquals("La Règle du Jeu", entity.getTitle());
    }

    @Test
    public void testXmlAccepts() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/xml/accepts"));
        Builder request = target.request();
        request.accept(APPLICATION_XML_UTF16_TYPE);

        String str = "<?xml version=\"1.0\"?>\r"
                + "<charSetFavoriteMovieXmlRootElement><title>La Règle du Jeu</title></charSetFavoriteMovieXmlRootElement>";
        log.info(str);
        log.info("client default charset: " + Charset.defaultCharset());

        Response response = request.post(Entity.entity(str, APPLICATION_XML_UTF16_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        CharSetFavoriteMovieXmlRootElement entity = response.readEntity(CharSetFavoriteMovieXmlRootElement.class);
        log.info("Result: " + entity);
        log.info("title: " + entity.getTitle());
        Assertions.assertEquals("La Règle du Jeu", entity.getTitle());
    }
}
