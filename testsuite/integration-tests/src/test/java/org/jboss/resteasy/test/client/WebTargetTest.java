package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.resource.WebTargetResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WebTargetTest extends ClientTestBase{

    static Client client;

    @BeforeClass
    public static void before() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WebTargetTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WebTargetResource.class);
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance
     * @tpPassCrit Original web target instance stayed untouched.
     * @tpProcedure  <ul>
     *              <li>Create WebTarget Instance from another base WebTarget instance</li>
     *              <li>Resolve templates in the uri</li>
     *              <li>Resolve "id" template, resolve "username" template, resolve again "id" template which should be ignored</li>
     *              <li>Resolve "username" template, create separate WebTarget instance, again resolve "username" template, resolve "id" twice</li>
     *              <li>Check original web target instance stayed untouched</li>
     *              </ul>
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplate() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        String result = created.resolveTemplate("id", "1").resolveTemplate("username", "test").request().get(String.class);
        Assert.assertEquals("username: test, 1", result);

        String result2 = created.resolveTemplate("id", "2").resolveTemplate("username", "test").resolveTemplate("id", "ignore").request().get(String.class);
        Assert.assertEquals("username: test, 2", result2);

        WebTarget modified = created.resolveTemplate("username", "test");
        String result3 = modified.resolveTemplate("username", "ignore").resolveTemplate("id", "3").resolveTemplate("id", "ignore").request().get(String.class);
        Assert.assertEquals("username: test, 3", result3);

        // Original Web target remains remains same
        Assert.assertEquals(generateURL("/") + "users/{username}" + "/{id}", created.getUriBuilder().toTemplate());
    }

    /**
     * @tpTestDetails Create WebTarget instance and call resolveTemplate with 'null' parameter
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testResolveTemplateNull() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        created.resolveTemplate(null, null);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, slash in the path.
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateSlash() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");
        String result = created.resolveTemplate("id", "1", false).resolveTemplate("username", "te//st", true).request().get(String.class);
        Assert.assertEquals("username: te//st, 1", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     * from decoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateDecoded() {
        final String a = "a%20%3F/*/";
        final String b = "/b/";

        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        String r2 = created.resolveTemplate("username", a).resolveTemplate("id", b).getUri().toString();
        Assert.assertEquals(generateURL("/") + "users/a%2520%253F%2F*%2F/%2Fb%2F", r2);

        String result = created.resolveTemplate("id", b).resolveTemplate("username", a).request().get(String.class);
        Assert.assertEquals("username: a%20%3F/*/, /b/", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve template
     * from encoded characters
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplateEncoded() {
        final String a = "a%20%3F/*/";
        final String b = "/b/";

        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        String result_encoded = created.resolveTemplateFromEncoded("id", b).resolveTemplateFromEncoded("username", a).request().get(String.class);
        Assert.assertEquals("username: a ?/*/, /b/", result_encoded);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve multiple templates at once.
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplates() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("question", "WHY");
        values.put("id", "1");
        values.put("username", "test");
        values.put("unknown", "none");

        String result = created.resolveTemplates(values).request().get(String.class);
        Assert.assertEquals("username: test, 1, WHY", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve templates with empty map
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplatesEmptyMap() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();

        WebTarget result = created.resolveTemplates(values);
        Assert.assertEquals(result, created);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve multiple templates
     * at once, path includes slash
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplatesSlash() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("question", "/WHY/");
        values.put("id", "1");
        values.put("username", "//test");
        values.put("unknown", "none");

        String result = created.resolveTemplates(values, true).request().get(String.class);
        Assert.assertEquals("username: //test, 1, /WHY/", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, resolve multiple
     * templates at once, encode values
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolveTemplatesEncoded() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}/{question}/{question}");

        // Create and fill map
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("question", "hello%20world");
        values.put("id", "%1");
        values.put("username", "ab%5Ec");
        values.put("unknown", "none");

        String result = created.resolveTemplatesFromEncoded(values).request().get(String.class);
        Assert.assertEquals("username: ab^c, %1, hello world", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, add and remove query params
     * @tpPassCrit Response from the server matches the pattern
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamAddAndRemove() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("param/{id}");
        String result = null;

        result = created.queryParam("q", "a").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a], k: []", result);

        result = created.queryParam("q", "a", "b").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a, b], k: []", result);

        result = created.queryParam("q", "a", "b").queryParam("k", "c", "d").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a, b], k: [c, d]", result);

        result = created.queryParam("q", "a", "b").queryParam("q", (Object) null).resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [], k: []", result);

        result = created.queryParam("q", "a").queryParam("k", "b").queryParam("q", (Object) null).resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [], k: [b]", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, test NullPointerException
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testQueryParamNullPointer() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("param/{id}");

        created.queryParam("q", "a", null, "b", null);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, change the URI by calling matrixParam()
     * @tpPassCrit Response from the server matches the pattern
     * @tpProcedure  <ul>
     *              <li>Create WebTarget Instance from another base WebTarget instance, change the URI by calling matrixParam()</li>
     *              <li>Add two different matrix params, add path, add matrix param with already existing name
     *              and remove it, send the resulting request to the server</li>
     *              <li>Add matrix param already in the path and send the resulting request to the server,
     *              verify tha  both values from matrix param are present</li>
     *              </ul>
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamModification() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("matrix/{id}");
        WebTarget modified;

        modified = created.matrixParam("m1", "abcd");
        modified = modified.matrixParam("m2", "cdef");
        modified = modified.path("path");
        modified = modified.matrixParam("m1", "abcd2");
        modified = modified.matrixParam("m1", new Object[]{null});
        String result = modified.resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, m1: [abcd], m2: [cdef]", result);

        modified = modified.matrixParam("m1", "abcd2");
        result = modified.resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, m1: [abcd, abcd2], m2: [cdef]", result);
    }

    /**
     * @tpTestDetails Create WebTarget Instance from another base WebTarget instance, call MatrixParam with null argument
     * @tpPassCrit NullPointerException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = NullPointerException.class)
    public void testMatrixParamNullPointer() {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("matrix/{id}");

        created.matrixParam("m1", "a", null, "b", null);
    }
}
