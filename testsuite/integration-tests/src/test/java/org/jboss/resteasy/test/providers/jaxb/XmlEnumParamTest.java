package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlEnumParamLocation;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlEnumParamResource;
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
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XmlEnumParamTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlEnumParamTest.class.getSimpleName());
        war.addClass(XmlEnumParamTest.class);
        return TestUtil.finishContainerPrepare(war, null, XmlEnumParamResource.class, XmlEnumParamLocation.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XmlEnumParamTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests xml enum param in the resource
     * @tpPassCrit The expected enum type is returned
     * @tpInfo RESTEASY-428
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlEnumParam() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/enum"));
        String response = target.queryParam("loc", "north").request().get(String.class);
        Assertions.assertEquals("NORTH", response.toUpperCase(),
                "The response doesn't contain expected enum type");
    }

}
