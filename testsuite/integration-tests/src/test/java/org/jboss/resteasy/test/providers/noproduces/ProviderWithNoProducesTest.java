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
        // TODO (jrp) This fixes these tests because it chooses the foo/bar over the bar/foo. The
        // TODO (jrp) spirit of this test is to check that foo/bar is chosen. However, with the spec algorithm, it would
        // TODO (jrp) choose bar/foo if the q=1.0 is assigned there. When choosing the type from the
        // TODO (jrp) accept header, we should not be checking whether MBW.isWritable(). The spec
        // TODO (jrp) https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1#determine_response_type
        // TODO (jrp) does not indicate this needs to be tested when determining the response type. It does, however,
        // TODO (jrp) indicate later if there is a MBW https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1#message_body_writer.
        // TODO (jrp) Therefore, the assertion that the chosen media type will be foo/bar with is invalid based on what isWritable() returns.
        Response response = target.request().accept("foo/bar;q=1.0, bar/foo;q=0.9").get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ProviderWithNoProducesMessageBodyWriter",
                response.readEntity(String.class), "Wrong response content");
        client.close();
    }
}
