package org.jboss.resteasy.test.providers.yaml;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.yaml.resource.YamlProviderNestedObject;
import org.jboss.resteasy.test.providers.yaml.resource.YamlProviderObject;
import org.jboss.resteasy.test.providers.yaml.resource.YamlProviderResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * @tpSubChapter Yaml provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class YamlProviderTest {

    protected static final Logger logger = Logger.getLogger(YamlProviderTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(YamlProviderTest.class.getSimpleName());
        war.addClass(YamlProviderTest.class).addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, YamlProviderResource.class, YamlProviderObject.class,
                YamlProviderNestedObject.class);
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
        return PortProviderUtil.generateURL(path, YamlProviderTest.class.getSimpleName());
    }


    /**
     * @tpTestDetails Client sends GET request for yaml annotated resource. It is asserted that response contains yaml header.
     * @tpPassCrit The resource returns yaml entity same as in the original request entity
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGet() throws Exception {
        WebTarget target = client.target(generateURL("/yaml"));
        Response response = target.request().get();
        String stringResponse = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response doesn't contain correct yaml header",
                "text/x-yaml;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        YamlProviderObject o1 = YamlProviderResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Assert.assertEquals("The entity response doesn't match the original request", s1, stringResponse);
    }

    /**
     * @tpTestDetails Client sends POST request for yaml annotated resource. It is asserted that response contains yaml header.
     * @tpPassCrit The resource returns yaml entity same as in the original request entity
     * @tpInfo JBEAP-1047
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() throws Exception {
        WebTarget target = client.target(generateURL("/yaml"));
        YamlProviderObject o1 = YamlProviderResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Response response = target.request().post(Entity.entity(s1, "text/x-yaml"));
        String stringResponse = response.readEntity(String.class);
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-1047"), HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response doesn't contain correct yaml header",
                "text/x-yaml;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals("The entity response doesn't match the original request", s1, stringResponse);
    }

    /**
     * @tpTestDetails Client sends POST request for yaml annotated resource with the entity in incorrect format
     * @tpPassCrit The response code is Internal server error (500)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPost() throws Exception {
        WebTarget target = client.target(generateURL("/yaml"));
        Response response = target.request().post(Entity.entity("---! bad", "text/x-yaml"));
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-7614", "Response code BAD_REQUEST (400) expected"), HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request for yaml with list of string
     * @tpPassCrit The resource returns yaml entity same as in the original request entity
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostList() throws Exception {
        List<String> data = Arrays.asList("a", "b", "c");
        String s1 = new Yaml().dump(data).trim();

        WebTarget target = client.target(generateURL("/yaml/list"));
        Response response = target.request().post(Entity.entity(s1, "text/x-yaml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("text/plain;charset=UTF-8", response.getHeaderString("Content-Type"));
        Assert.assertEquals(s1, response.readEntity(String.class).trim());
    }

}
