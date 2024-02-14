package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.asynch.resource.AsyncTimeoutResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncTimeoutTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(AsyncTimeoutTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncTimeoutResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncTimeoutTest.class.getSimpleName());
    }

    @Test
    public void testAsyncTimeOut() throws Exception {
        WebTarget base = client.target(generateURL("/async"));
        Response response = base.request().get();
        Assertions.assertEquals("Async hello", response.readEntity(String.class));
        Response timeoutRes = client.target(generateURL("/timeout")).request().get();
        Assertions.assertTrue(timeoutRes.readEntity(String.class).contains("false"), "Wrongly call Timeout Handler");
        response.close();
    }

    @Test
    public void testAsyncTimeoutHandlerExtendsTimeOut() throws Exception {
        WebTarget base = client.target(generateURL("/extendedTimeout"));
        long startTime = System.nanoTime();
        Response response = base.request().get();
        long elapsedTime = System.nanoTime() - startTime;
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Extended timeout hello", response.readEntity(String.class));
        Assertions.assertTrue(elapsedTime > 4000000000L, "Timeout fired too quickly");
        response.close();
    }

    @Test
    public void testResumeAfterSettingAsyncTimeoutHandler() throws Exception {
        WebTarget base = client.target(generateURL("/resumeAfterSettingTimeoutHandler"));
        Response response = base.request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("From initial", response.readEntity(String.class));
        response.close();
    }
}
