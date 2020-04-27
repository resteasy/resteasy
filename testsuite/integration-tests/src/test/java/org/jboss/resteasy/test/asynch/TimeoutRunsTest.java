package org.jboss.resteasy.test.asynch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.asynch.resource.TimeoutRunsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
@RunAsClient
public class TimeoutRunsTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TimeoutRunsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TimeoutRunsResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, TimeoutRunsTest.class.getSimpleName());
    }

    @Test
    public void testSuccess() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/async"));
        Response response = base.request().get();
        Assert.assertEquals("Async hello", response.readEntity(String.class));
        Response timeoutRes = client.target(generateURL("/timeout")).request().get();
        Assert.assertTrue("Wrongly call Timeout Handler", timeoutRes.readEntity(String.class).contains("false"));
        response.close();
        client.close();
    }
}
