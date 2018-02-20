package org.jboss.resteasy.test.providers.yaml;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.yaml.resource.YamlPojoBindingNestedObject;
import org.jboss.resteasy.test.providers.yaml.resource.YamlPojoBindingObject;
import org.jboss.resteasy.test.providers.yaml.resource.YamlResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Yaml provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1223
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class YamlPojoBindingTest {
    private static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    private static final String HEADER_ERROR_MSG = "Wrong content-type header";

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(YamlPojoBindingTest.class.getSimpleName());
        war.addClasses(YamlPojoBindingNestedObject.class, YamlPojoBindingObject.class)
        .addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, YamlResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, YamlPojoBindingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails GET method test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGet() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget get = client.target(generateURL("/yaml"));
        Response response = get.request().get();

        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals(HEADER_ERROR_MSG, "text/x-yaml;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));

        String s = response.readEntity(String.class);
        YamlPojoBindingObject o1 = YamlResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Assert.assertEquals(RESPONSE_ERROR_MSG, s1, s);
        response.close();
    }

    /**
     * @tpTestDetails POST method test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() throws Exception {

        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(generateURL("/yaml"));

        YamlPojoBindingObject o1 = YamlResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Response response = post.request().post(Entity.entity(s1, "text/x-yaml"));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(HEADER_ERROR_MSG, "text/x-yaml;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals(RESPONSE_ERROR_MSG, s1, response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails POST method test. Wrong request, error expected.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPost() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(generateURL("/yaml"));
        Response response = post.request().post(Entity.entity("---! bad", "text/x-yaml"));
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-7614", "Response code BAD_REQUEST (400) expected"), HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails POST method test. List is in request and in response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostList() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(generateURL("/yaml/list"));

        List<String> data = Arrays.asList("a", "b", "c");
        String s1 = new Yaml().dump(data).trim();

        Response response = post.request().post(Entity.entity(s1, "text/x-yaml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals(HEADER_ERROR_MSG, "text/plain;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals(RESPONSE_ERROR_MSG, s1, response.readEntity(String.class).trim());
    }

}
