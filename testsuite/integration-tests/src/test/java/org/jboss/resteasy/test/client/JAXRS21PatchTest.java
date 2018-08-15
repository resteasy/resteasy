package org.jboss.resteasy.test.client;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.resource.JAXRS21SyncInvokeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class JAXRS21PatchTest extends ClientTestBase {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JAXRS21PatchTest.class.getSimpleName());
        war.addClass(JAXRS21PatchTest.class);
        return TestUtil.finishContainerPrepare(war, null, JAXRS21SyncInvokeResource.class);
    }

    @Before
    public void init() {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends http PATCH request with invoke() method
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testMethods() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().method(HttpMethod.PATCH, Entity.text("hello"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().method(HttpMethod.PATCH, Entity.text("hello"), String.class);
            Assert.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends http PATCH request with method() method
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testInvoke() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().build(HttpMethod.PATCH, Entity.text("hello")).invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().build(HttpMethod.PATCH, Entity.text("hello")).invoke(String.class);
            Assert.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Check that PATCH is present in OPTIONS response if the resource supports PATCH method
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testOptionsContainsAllowPatch() throws Exception {
        Response res = client.target(generateURL("/test")).request().options();
        Assert.assertThat(res.getHeaderString("Allow"), CoreMatchers.containsString("PATCH"));
        res.close();
    }

    /**
     * @tpTestDetails Check that OPTIONS response contains Accept-Patch header with supported PATCH format descriptors
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testOptionsContainsAcceptPatch() throws Exception {
        Response res = client.target(generateURL("/test")).request().options();
        Assert.assertEquals("text/plain", res.getHeaderString("Accept-Patch"));
        res.close();
    }

    /**
     * @tpTestDetails Check http headers in the response after successful PATCH request
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testPatchHeaders() throws Exception {
        Response res = client.target(generateURL("/test")).request().method(HttpMethod.PATCH, Entity.text("hello"));
        MultivaluedMap<String, String> stringHeaders = res.getStringHeaders();
        stringHeaders.forEach((k,v)-> {
            if (k.equals("Content-type")) Assert.assertEquals("text/plain;charset=UTF-8", v);
        });
    }
}
