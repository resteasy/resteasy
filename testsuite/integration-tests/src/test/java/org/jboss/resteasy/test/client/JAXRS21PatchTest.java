package org.jboss.resteasy.test.client;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

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

    @Test
    public void testMethods() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().patch(Entity.text("hello"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().patch(Entity.text("hello"), String.class);
            Assert.assertEquals("patch hello", entity);
        }
    }

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
}
