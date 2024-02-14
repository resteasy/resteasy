package org.jboss.resteasy.test.providers.noproduces;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.providers.noproduces.resource.Foo;
import org.jboss.resteasy.test.providers.noproduces.resource.ProviderWithNoProducesMessageBodyWriter;
import org.jboss.resteasy.test.providers.noproduces.resource.ProviderWithNoProducesResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter MessageBodyWriters with no @Produces annotation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2232
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProviderWithNoProducesTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProviderWithNoProducesTest.class.getSimpleName());
        war.addClass(Foo.class);
        war.addAsWebInfResource(ProviderWithNoProducesTest.class.getPackage(), "ProviderWithNoProduces_web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, ProviderWithNoProducesResource.class,
                ProviderWithNoProducesMessageBodyWriter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProviderWithNoProducesTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails ProviderWithNoProducesMessageBodyWriter, which has no @Pruduces annotation, should
     *                be williing to write a Foo with media type "foo/bar" but not "bar/foo".
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testWriteFoo() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/foo"));
        Response response = target.request().accept("foo/bar;q=0.9, bar/foo;q=1.0").get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ProviderWithNoProducesMessageBodyWriter",
                response.readEntity(String.class), "Wrong response content");
        client.close();
    }
}
