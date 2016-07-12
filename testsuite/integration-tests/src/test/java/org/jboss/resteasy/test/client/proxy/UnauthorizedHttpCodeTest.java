package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.UnauthorizedHttpCodeProxy;
import org.jboss.resteasy.test.client.proxy.resource.UnauthorizedHttpCodeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.NotAuthorizedException;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-575
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UnauthorizedHttpCodeTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(UnauthorizedHttpCodeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, UnauthorizedHttpCodeResource.class);
    }

    /**
     * @tpTestDetails Get 401 http code via proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        UnauthorizedHttpCodeProxy proxy = client.target(PortProviderUtil.generateURL("/", UnauthorizedHttpCodeTest.class.getSimpleName())).proxy(UnauthorizedHttpCodeProxy.class);
        try {
            proxy.getFoo();
        } catch (NotAuthorizedException e) {
            Assert.assertEquals(e.getResponse().getStatus(), HttpResponseCodes.SC_UNAUTHORIZED);
            String val = e.getResponse().readEntity(String.class);
            Assert.assertEquals("Wrong content of response", "hello", val);
        }
        client.close();
    }

}
