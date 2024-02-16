package org.jboss.resteasy.test.providers.jaxb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.CharacterSetData;
import org.jboss.resteasy.test.providers.jaxb.resource.CharacterSetResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CharacterSetTest {

    private final String[] characterSets = { "US-ASCII", "UTF-8", "ISO-8859-1" };
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CharacterSetTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CharacterSetData.class, CharacterSetResource.class);
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
        return PortProviderUtil.generateURL(path, CharacterSetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests if correct Variant is chosen for given combination of mediatype xml and charsets.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void variantSelection() throws URISyntaxException {
        assertCharset("/variant-selection");
    }

    private void assertCharset(String path) throws URISyntaxException {
        for (String characterSet : characterSets) {
            ResteasyWebTarget target = client.target(generateURL(path));
            Response response = target.request().accept("application/xml").header("Accept-Charset", characterSet).get();

            assertEquals(200, response.getStatus(), "Status code");

            String contentType = response.getHeaders().getFirst("Content-Type").toString();
            String charsetPattern = "application/xml\\s*;\\s*charset\\s*=\\s*\"?" + characterSet + "\"?";
            String charsetErrorMessage = contentType + " does not match " + charsetPattern;
            assertTrue(contentType.matches(charsetPattern), charsetErrorMessage);

            String xml = response.readEntity(String.class);
            String encodingPattern = "<\\?xml[^>]*encoding\\s*=\\s*['\"]" + characterSet + "['\"].*";
            String encodingErrorMessage = xml + " does not match " + encodingPattern;
            assertTrue(xml.matches(encodingPattern), encodingErrorMessage);

            response.close();
        }
    }

}
