package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsRootProxy;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxy;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxyRootImpl;
import org.jboss.resteasy.test.resource.basic.resource.CovariantReturnSubresourceLocatorsSubProxySubImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test return value of covariant with locators.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(PortProviderUtil.generateURL("/path/sub/xyz",
                CovariantReturnSubresourceLocatorsTest.class.getSimpleName())).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Boo! - xyz", response.readEntity(String.class),
                "Wrong content of response");
        response.close();
        client.close();
    }
}
