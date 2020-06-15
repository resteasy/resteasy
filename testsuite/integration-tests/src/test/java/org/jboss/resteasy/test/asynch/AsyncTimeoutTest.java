package org.jboss.resteasy.test.asynch;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.asynch.resource.AsyncTimeoutResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncTimeoutTest {
    static ResteasyClient client;
    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(AsyncTimeoutTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncTimeoutResource.class);
    }

    @BeforeClass
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterClass
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
        Assert.assertEquals("Async hello", response.readEntity(String.class));
        Response timeoutRes = client.target(generateURL("/timeout")).request().get();
        Assert.assertTrue("Wrongly call Timeout Handler", timeoutRes.readEntity(String.class).contains("false"));
        response.close();
    }

}
