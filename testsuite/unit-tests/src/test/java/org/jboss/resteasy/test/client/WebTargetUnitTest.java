package org.jboss.resteasy.test.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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

    @BeforeClass
    public static void setupClient() {
        client = ClientBuilder.newClient();
        base = client.target(generateURL("/") + "users/{username}");
        created = base.path("{id}");
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     * from decoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateDecoded() {
        final String usernameDecoded = "a%20%3F/*/";
        final String idDecoded = "/b/";

        String result = created.resolveTemplate("username", usernameDecoded).resolveTemplate("id", idDecoded).getUri().toString();
        // The asserted string "a%2520%253F%2F*%2F/%2Fb%2F" is made from "a%20%3F/*//b// as:
        // % stands for %25 in hex ASCII
        // / stands for %2F in hex ASCII
        Assert.assertEquals("The parameters were not treated as decoded correctly", generateURL("/") + "users/a%2520%253F%2F*%2F/%2Fb%2F", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     * from encoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateEncoded() {
        final String usernameEncoded = "a%20%3F/*/";
        final String idEncoded = "/b/";

        String result = created.resolveTemplateFromEncoded("username", usernameEncoded).resolveTemplateFromEncoded("id", idEncoded).getUri().toString();
        Assert.assertEquals("The parameters were not treated as encoded correctly", generateURL("/") + "users/a%20%3F%2F*%2F/%2Fb%2F", result);
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
        Assert.assertEquals(result, created);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, test resolveTemplate()
     * for NullPointerException
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testResolveTemplateNull() {
        created.resolveTemplate(null, null);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance,
     * test queryParam() NullPointerException
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testQueryParamNullPointer() {
        WebTarget created = base.path("param/{id}");

        created.queryParam("q", "a", null, "b", null);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, call MatrixParam with null argument
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testMatrixParamNullPointer() {
        WebTarget created = base.path("matrix/{id}");

        created.matrixParam("m1", "a", null, "b", null);
    }
}
