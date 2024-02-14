package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorIntf1;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorIntf2;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorResource;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorSubresource;
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
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultiInterfaceResLocatorTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiInterfaceResLocatorTest.class.getSimpleName());
        war.addClass(MultiInterfaceResLocatorIntf1.class);
        war.addClass(MultiInterfaceResLocatorIntf2.class);
        return TestUtil.finishContainerPrepare(war, null, MultiInterfaceResLocatorResource.class,
                MultiInterfaceResLocatorSubresource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultiInterfaceResLocatorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for resource with more interfaces.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/hello1/")).request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("resourceMethod1", entity, "Wrong content of response");

        response = client.target(generateURL("/test/hello2/")).request().get();
        entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("resourceMethod2", entity, "Wrong content of response");
        client.close();
    }
}
