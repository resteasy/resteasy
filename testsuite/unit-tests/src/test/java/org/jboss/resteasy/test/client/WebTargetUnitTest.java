package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @version $Revision: 1 $
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 */
public class WebTargetUnitTest {

    static Client client;
    static WebTarget base;
    static WebTarget created;

    @BeforeAll
    public static void setupClient() {
        client = ClientBuilder.newClient();
        base = client.target(generateURL("/") + "users/{username}");
        created = base.path("{id}");
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     *                from decoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateDecoded() {
        final String usernameDecoded = "a%20%3F/*/";
        final String idDecoded = "/b/";

        String result = created.resolveTemplate("username", usernameDecoded).resolveTemplate("id", idDecoded).getUri()
                .toString();
        // The asserted string "a%2520%253F%2F*%2F/%2Fb%2F" is made from "a%20%3F/*//b// as:
        // % stands for %25 in hex ASCII
        // / stands for %2F in hex ASCII
        Assertions.assertEquals(generateURL("/") + "users/a%2520%253F%2F*%2F/%2Fb%2F", result,
                "The parameters were not treated as decoded correctly");
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     *                from encoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateEncoded() {
        final String usernameEncoded = "a%20%3F/*/";
        final String idEncoded = "/b/";

        String result = created.resolveTemplateFromEncoded("username", usernameEncoded)
                .resolveTemplateFromEncoded("id", idEncoded).getUri().toString();
        Assertions.assertEquals(generateURL("/") + "users/a%20%3F%2F*%2F/%2Fb%2F", result,
                "The parameters were not treated as encoded correctly");
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve templates with empty map
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplatesEmptyMap() {
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();

        WebTarget result = created.resolveTemplates(values);
        Assertions.assertEquals(result, created);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, test resolveTemplate()
     *                for NullPointerException
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateNull() {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class,
                () -> {
                    created.resolveTemplate(null, null);
                });
        Assertions.assertTrue(thrown instanceof NullPointerException);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance,
     *                test queryParam() NullPointerException
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamNullPointer() {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class,
                () -> {
                    WebTarget created = base.path("param/{id}");

                    created.queryParam("q", "a", null, "b", null);
                });
        Assertions.assertTrue(thrown instanceof NullPointerException);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, call MatrixParam with null argument
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamNullPointer() {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class,
                () -> {
                    WebTarget created = base.path("matrix/{id}");

                    created.matrixParam("m1", "a", null, "b", null);
                });
        Assertions.assertTrue(thrown instanceof NullPointerException);
    }
}
