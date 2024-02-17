package org.jboss.resteasy.test.client;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.JAXRS21SyncInvokeResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JAXRS21PatchTest extends ClientTestBase {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JAXRS21PatchTest.class.getSimpleName());
        war.addClass(JAXRS21PatchTest.class);
        return TestUtil.finishContainerPrepare(war, null, JAXRS21SyncInvokeResource.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
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
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().method(HttpMethod.PATCH, Entity.text("hello"),
                    String.class);
            Assertions.assertEquals("patch hello", entity);
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
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().build(HttpMethod.PATCH, Entity.text("hello"))
                    .invoke(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Check that PATCH is present in OPTIONS response if the resource supports PATCH method
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testOptionsContainsAllowPatch() throws Exception {
        Response res = client.target(generateURL("/test")).request().options();
        MatcherAssert.assertThat(res.getHeaderString("Allow"), CoreMatchers.containsString("PATCH"));
        res.close();
    }

    /**
     * @tpTestDetails Check that OPTIONS response contains Accept-Patch header with supported PATCH format descriptors
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testOptionsContainsAcceptPatch() throws Exception {
        Response res = client.target(generateURL("/test")).request().options();
        Assertions.assertEquals("text/plain", res.getHeaderString("Accept-Patch"));
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
        stringHeaders.forEach((k, v) -> {
            if (k.equals("Content-type"))
                Assertions.assertEquals("text/plain;charset=UTF-8", v);
        });
    }
}
