package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.resource.SyncInvokeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SyncInvokeTest extends ClientTestBase{

    @java.lang.annotation.Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("PATCH")
    public @interface PATCH {
    }

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SyncInvokeTest.class.getSimpleName());
        war.addClass(SyncInvokeTest.class);
        return TestUtil.finishContainerPrepare(war, null, SyncInvokeResource.class);
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
     * @tpTestDetails Client sends  GET, PUT, DELETE, POST and custom defined requests. First request expects
     * Response object in return, the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMethods() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("get", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().get(String.class);
            Assert.assertEquals("get", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().delete();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("delete", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().delete(String.class);
            Assert.assertEquals("delete", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().put(Entity.text("hello"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("put hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().put(Entity.text("hello"), String.class);
            Assert.assertEquals("put hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().post(Entity.text("hello"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("post hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().post(Entity.text("hello"), String.class);
            Assert.assertEquals("post hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"), String.class);
            Assert.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends  GET, PUT, DELETE, POST and custom defined requests. The request is send using
     * invoke() method. First request expects Response object in return, the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInvoke() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().buildGet().invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("get", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildGet().invoke(String.class);
            Assert.assertEquals("get", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildDelete().invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("delete", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildDelete().invoke(String.class);
            Assert.assertEquals("delete", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("put hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke(String.class);
            Assert.assertEquals("put hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("post hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke(String.class);
            Assert.assertEquals("post hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).invoke();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).invoke(String.class);
            Assert.assertEquals("patch hello", entity);
        }
    }
}
