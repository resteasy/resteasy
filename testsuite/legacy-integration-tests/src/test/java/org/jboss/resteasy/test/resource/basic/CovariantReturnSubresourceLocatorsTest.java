package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsRootProxy;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxy;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxyRootImpl;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxySubImpl;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test return value of covariant with locators.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CovariantReturnSubresourceLocatorsTest {

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CovariantReturnSubresourceLocatorsTest.class.getSimpleName());
        war.addClasses(CovariantReturnSubresourceLocatorsRootProxy.class, CovariantReturnSubresourceLocatorsSubProxy.class);
        return TestUtil.finishContainerPrepare(war, null, CovariantReturnSubresourceLocatorsSubProxyRootImpl.class,
                CovariantReturnSubresourceLocatorsSubProxySubImpl.class);
    }

    /**
     * @tpTestDetails Test basic path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void basicTest() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(PortProviderUtil.generateURL("/path/sub/xyz",
                CovariantReturnSubresourceLocatorsTest.class.getSimpleName())).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", "Boo! - xyz", response.readEntity(String.class));
        response.close();
        client.close();
    }
}