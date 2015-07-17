package org.jboss.resteasy.test.nextgen.client;

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
 * Tests for methods of WebTarget
 *
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @version $Revision: 1 $
 */
public class WebTargetUnitTest {

    static Client client;

    @BeforeClass
    public static void setupClient()
    {
        client = ClientBuilder.newClient();

    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, resolve template from decoded characters
     */
    @Test
    public void testResolveTemplateDecoded()
    {
        final String a = "a%20%3F/*/";
        final String b = "/b/";

        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        String r2 = created.resolveTemplate("username", a).resolveTemplate("id", b).getUri().toString();
        Assert.assertEquals(generateURL("/") + "users/a%2520%253F%2F*%2F/%2Fb%2F", r2);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, resolve template from encoded characters
     */
    @Test
    public void testResolveTemplateEncoded()
    {
        final String a = "a%20%3F/*/";
        final String b = "/b/";

        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        String r = created.resolveTemplateFromEncoded("username", a).resolveTemplateFromEncoded("id", b).getUri().toString();
        Assert.assertEquals(generateURL("/") + "users/a%20%3F%2F*%2F/%2Fb%2F", r);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, resolve templates with empty map
     */
    @Test
    public void testResolveTemplatesEmptyMap()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();

        WebTarget result = created.resolveTemplates(values);
        Assert.assertEquals(result, created);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, test resolveTemplate for NullPointerException
     */
    @Test(expected=NullPointerException.class)
    public void testResolveTemplateNull()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        created.resolveTemplate(null, null);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, test NullPointerException
     */
    @Test(expected=NullPointerException.class)
    public void testQueryParamNullPointer()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("param/{id}");

        created.queryParam("q", "a", null, "b", null);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, call MatrixParam with null argument
     */
    @Test(expected=NullPointerException.class)
    public void testMatrixParamNullPointer()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("matrix/{id}");

        created.matrixParam("m1", "a", null, "b", null);
    }
}
