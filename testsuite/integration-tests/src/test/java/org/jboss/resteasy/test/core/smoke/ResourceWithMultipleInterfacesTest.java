package org.jboss.resteasy.test.core.smoke;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesEmpty;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesIntA;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesRootResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke test for resource with multiple interfaces.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceWithMultipleInterfacesTest {

    static ResteasyClient client;

    @Deployment(name = "LocatingResource")
    public static Archive<?> deployLocatingResource() {
        WebArchive war = TestUtil.prepareArchive(ResourceWithMultipleInterfacesTest.class.getSimpleName());
        war.addClass(ResourceWithMultipleInterfacesIntA.class);
        war.addClass(ResourceWithMultipleInterfacesEmpty.class);
        return TestUtil.finishContainerPrepare(war, null, ResourceWithMultipleInterfacesRootResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceWithMultipleInterfacesTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check result from resource with multiple interfaces.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        ResourceWithMultipleInterfacesIntA proxy = client.target(generateURL("/"))
                .proxyBuilder(ResourceWithMultipleInterfacesIntA.class).build();
        Assertions.assertEquals("FOO", proxy.getFoo(), "Wrong client answer.");
    }
}
