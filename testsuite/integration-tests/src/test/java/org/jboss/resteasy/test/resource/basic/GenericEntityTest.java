package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntityDoubleWriter;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntityResource;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntitytFloatWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GenericEntityTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GenericEntityTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, GenericEntityResource.class, GenericEntityDoubleWriter.class,
                GenericEntitytFloatWriter.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GenericEntityTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resource returning GenericEntity with custom MessageBodyWriter returning double values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubles() {
        WebTarget base = client.target(generateURL("/doubles"));
        try {
            Response response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assertions.assertEquals("45.0D 50.0D ", body,
                    "The response doesn't contain the expected entity");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Resource returning GenericEntity with custom MessageBodyWriter returning float values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloats() {
        WebTarget base = client.target(generateURL("/floats"));
        try {
            Response response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assertions.assertEquals("45.0F 50.0F ", body,
                    "The response doesn't contain the expected entity");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
