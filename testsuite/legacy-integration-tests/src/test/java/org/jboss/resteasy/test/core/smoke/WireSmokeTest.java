package org.jboss.resteasy.test.core.smoke;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.smoke.resource.WireSmokeLocatingResource;
import org.jboss.resteasy.test.core.smoke.resource.WireSmokeSimpleResource;
import org.jboss.resteasy.test.core.smoke.resource.WireSmokeSimpleSubresource;
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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check basic resource function.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WireSmokeTest {

    static Client client;
    private static final String WRONG_RESPONSE = "Wrong response content.";

    @Deployment(name = "LocatingResource")
    public static Archive<?> deployLocatingResource() {
        WebArchive war = TestUtil.prepareArchive(WireSmokeLocatingResource.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WireSmokeLocatingResource.class, WireSmokeSimpleResource.class, WireSmokeSimpleSubresource.class);
    }

    @Deployment(name = "SimpleResource")
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(WireSmokeSimpleResource.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WireSmokeSimpleResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURLNoDefault(String path) {
        return PortProviderUtil.generateURL(path, WireSmokeSimpleResource.class.getSimpleName());
    }

    private String generateURLLocating(String path) {
        return PortProviderUtil.generateURL(path, WireSmokeLocatingResource.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check result from simple resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        {
            Response response = client.target(generateURLNoDefault("/basic")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "basic", response.readEntity(String.class));
        }
        {
            Response response = client.target(generateURLNoDefault("/basic")).request().put(Entity.entity("basic", "text/plain"));
            Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
            response.close();
        }
        {
            Response response = client.target(generateURLNoDefault("/queryParam")).queryParam("param", "hello world").request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "hello world", response.readEntity(String.class));
        }
        {
            Response response = client.target(generateURLNoDefault("/uriParam/1234")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "1234", response.readEntity(String.class));
        }
    }

    /**
     * @tpTestDetails Check result from more resources.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocatingResource() throws Exception {
        {
            Response response = client.target(generateURLLocating("/locating/basic")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "basic", response.readEntity(String.class));
        }
        {
            Response response = client.target(generateURLLocating("/locating/basic")).request().put(Entity.entity("basic", "text/plain"));
            Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
            response.close();
        }
        {
            Response response = client.target(generateURLLocating("/locating/queryParam")).queryParam("param", "hello world").request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "hello world", response.readEntity(String.class));
        }
        {
            Response response = client.target(generateURLLocating("/locating/uriParam/1234")).queryParam("param", "hello world").request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(WRONG_RESPONSE, "1234", response.readEntity(String.class));
        }
        {
           Response response = client.target(generateURLLocating("/locating/uriParam/x1234")).queryParam("param", "hello world").request().get();
           Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
           response.close();
        }
        {
           Response response = client.target(generateURLLocating("/locating/notmatching")).request().get();
           Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
           response.close();
        }
        {
           Response response = client.target(generateURLLocating("/subresource/subresource/subresource/basic")).request().get();
           Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
           Assert.assertEquals("basic", response.readEntity(String.class));
        }
    }
}
