package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.client.proxy.resource.MediaTypeCaseSensitivityStuff;
import org.jboss.resteasy.test.client.proxy.resource.MediaTypeCaseSensitivityStuffProvider;
import org.jboss.resteasy.test.client.proxy.resource.MediaTypeCaseSensitivityStuffResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Regression test for RESTEASY-207
 * @tpSince RESTEasy 3.0.16
 */
@Disabled("RESTEASY-3450")
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MediaTypeCaseSensitivityTest {

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MediaTypeCaseSensitivityTest.class.getSimpleName());
        war.addClass(MediaTypeCaseSensitivityStuff.class);
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, MediaTypeCaseSensitivityStuffResource.class,
                MediaTypeCaseSensitivityStuffProvider.class);
    }

    private ResteasyProviderFactory factory;

    @BeforeEach
    public void setup() {
        // Create an instance and set it as the singleton to use
        factory = ResteasyProviderFactory.newInstance();
        ResteasyProviderFactory.setInstance(factory);
        RegisterBuiltin.register(factory);
    }

    @AfterEach
    public void cleanup() {
        // Clear the singleton
        ResteasyProviderFactory.clearInstanceIfEqual(factory);
    }

    /**
     * @tpTestDetails MediaType case sensitivity when matching MessageBodyReader
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testItPost() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/stuff", MediaTypeCaseSensitivityTest.class.getSimpleName()));
        Response response = base.request().post(Entity.entity("bill", "Application/Stuff"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails MediaType case sensitivity when matching MessageBodyReader, check the MessageBodyReader of
     *                the custom type is available on the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testItGet() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/stuff", MediaTypeCaseSensitivityTest.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
        client.close();
    }

}
