package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Tests using WebTarget instance to invoke request on the server.
 *
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @version $Revision: 1 $
 */
public class WebTargetTest extends BaseResourceTest
{
    @Path("/")
    public static class TestResource
    {
        @GET
        @Path("/users/{username}/{id}")
        @Produces("text/plain")
        public String get(@PathParam("username") String username, @PathParam("id") String id) {
            return "username: " + username + ", " + id;
        }

        @GET
        @Path("/users/{username}/{id}/{question}/{question}")
        @Produces("text/plain")
        public String getMultiple(@PathParam("username") String username, @PathParam("id") String id, @PathParam("question") String q) {
            return "username: " + username + ", " + id + ", " + q;
        }

        @GET
        @Path("/users/{username}/param/{id}")
        @Produces("text/plain")
        public String getParam(@PathParam("username") String username, @PathParam("id") String id, @QueryParam("q") List<String> q, @QueryParam("k") List<String> k) {
            return "username: " + username + ", " + id + ", q: " + q.toString() + ", k: " + k.toString();
        }

        @GET
        @Path("/users/{username}/matrix/{id}")
        @Produces("text/plain")
        public String getParamMatrix(@PathParam("username") String username, @PathParam("id") String id, @MatrixParam("m1") List<String> m1, @MatrixParam("m2") List<String> m2) {
            return "username: " + username + ", " + id + ", m1: " + m1.toString() + ", m2: " + m2.toString();
        }

        @GET
        @Path("/users/{username}/matrix/{id}/path")
        @Produces("text/plain")
        public String getParamMatrixPath(@PathParam("username") String username, @PathParam("id") String id, @MatrixParam("m1") List<String> m1, @MatrixParam("m2") List<String> m2) {
            return "username: " + username + ", " + id + ", m1: " + m1.toString() + ", m2: " + m2.toString();
        }
    }

    static Client client;

    @BeforeClass
    public static void setupClient()
    {
        client = ClientBuilder.newClient();
        addPerRequestResource(TestResource.class);

    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance.
     * Resolve templates in the uri.
     * Resolve "id" template, resolve "username" template, resolve again "id" template which should be ignored.
     * Resolve "username" template, create separate WebTarget instance, again resolve "username" template, resolve "id" twice.
     * Check original web target instance stayed untouched.
     */
    @Test
    public void testResolveTemplate()
    {
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

    @Test(expected=NullPointerException.class)
    public void testResolveTemplateNull()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");

        created.resolveTemplate(null, null);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, slash in the path.
     */
    @Test
    public void testResolveTemplateSlash()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("{id}");
        String result = created.resolveTemplate("id", "1", false).resolveTemplate("username", "te//st", true).request().get(String.class);
        Assert.assertEquals("username: te//st, 1", result);
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

        String result= created.resolveTemplate("id", b).resolveTemplate("username", a).request().get(String.class);
        Assert.assertEquals("username: a%20%3F/*/, /b/", result);
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

        String result_encoded = created.resolveTemplateFromEncoded("id", b).resolveTemplateFromEncoded("username", a).request().get(String.class);
        Assert.assertEquals("username: a ?/*/, /b/", result_encoded);
    }

    /*
     * Create WebTarget Instance from another base WebTarget instance, resolve multiple templates at once.
     */
    @Test
    public void testResolveTemplates()
    {
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
     * Create WebTarget Instance from another base WebTarget instance, resolve multiple templates at once, path includes slash
     */
    @Test
    public void testResolveTemplatesSlash()
    {
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

    /*
     * Create WebTarget Instance from another base WebTarget instance, resolve multiple templates at once, encode values
     */
    @Test
    public void testResolveTemplatesEncoded()
    {
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

    /*
     * Create WebTarget Instance from another base WebTarget instance, add and remove query params
     */
    @Test
    public void testQueryParamAddAndRemove()
    {
        WebTarget base = client.target(generateURL("/") + "users/{username}");
        WebTarget created = base.path("param/{id}");
        String result = null;

        result = created.queryParam("q", "a").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a], k: []", result);

        result = created.queryParam("q", "a", "b").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a, b], k: []", result);

        result = created.queryParam("q", "a", "b").queryParam("k", "c", "d").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [a, b], k: [c, d]", result);

        //bug?
        result = created.queryParam("q", "a", "b").queryParam("q").resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        //Assert.assertEquals("username: test, 1, q: [], k:[]", result);

        result = created.queryParam("q", "a", "b").queryParam("q", (Object) null).resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [], k: []", result);

        result = created.queryParam("q", "a").queryParam("k", "b").queryParam("q", (Object) null).resolveTemplate("username", "test").resolveTemplate("id", "1").request().get(String.class);
        Assert.assertEquals("username: test, 1, q: [], k: [b]", result);
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
     * Create WebTarget Instance from another base WebTarget instance, change the URI by calling matrixParam().
     * Add two different matrix params, add path, add matrix param with already existing name and remove it, send the resulting request to the server.
     * Add matrix param already in the path and send the resulting request to the server, verify tha  both values from matrix param are present.
     */
    @Test
    public void testMatrixParamModification()
    {
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
