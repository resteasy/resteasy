package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Product;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Resource;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectFilterModifier;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectWriterModifierFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JsonFilterWithSerlvetFilterTest {

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonFilterWithSerlvetFilterTest.class.getSimpleName());
        war.addClasses(ObjectFilterModifier.class, Jackson2Product.class, ObjectWriterModifierFilter.class);
        war.addAsManifestResource(
                new StringAsset(
                        "Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"),
                "MANIFEST.MF");
        war.addAsWebInfResource(JsonFilterWithSerlvetFilterTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonFilterWithSerlvetFilterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Filters fields from json response entity. Specifies the filter implementation class in web.xml.
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonString() throws Exception {
        Client client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        response.bufferEntity();
        Assertions.assertTrue(!response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"), "filter doesn't work");
        client.close();
    }
}
