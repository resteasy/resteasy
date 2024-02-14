package org.jboss.resteasy.test.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.SyncInvokeResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SyncInvokeTest extends ClientTestBase {

    @java.lang.annotation.Target({ ElementType.METHOD })
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

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET, PUT, DELETE, POST and custom defined requests. First request expects
     *                Response object in return, the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMethods() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("get", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().get(String.class);
            Assertions.assertEquals("get", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().delete();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("delete", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().delete(String.class);
            Assertions.assertEquals("delete", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().put(Entity.text("hello"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("put hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().put(Entity.text("hello"), String.class);
            Assertions.assertEquals("put hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().post(Entity.text("hello"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("post hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().post(Entity.text("hello"), String.class);
            Assertions.assertEquals("post hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().method("PATCH", Entity.text("hello"), String.class);
            Assertions.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends GET, PUT, DELETE, POST and custom defined requests. The request is send using
     *                invoke() method. First request expects Response object in return, the second expects String object in
     *                return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInvoke() throws Exception {
        {
            Response res = client.target(generateURL("/test")).request().buildGet().invoke();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("get", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildGet().invoke(String.class);
            Assertions.assertEquals("get", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildDelete().invoke();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("delete", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildDelete().invoke(String.class);
            Assertions.assertEquals("delete", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("put hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).invoke(String.class);
            Assertions.assertEquals("put hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("post hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).invoke(String.class);
            Assertions.assertEquals("post hello", entity);
        }
        {
            Response res = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).invoke();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
        {
            String entity = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello"))
                    .invoke(String.class);
            Assertions.assertEquals("patch hello", entity);
        }
    }
}
