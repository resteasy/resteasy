package org.jboss.resteasy.test.providers.jackson2;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonDisableTimeStampProducer;
import org.jboss.resteasy.test.providers.jackson2.resource.PortalStatusResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
//Test for https://issues.redhat.com/browse/RESTEASY-3456
public class JacksonProviderContextResolverTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JacksonProviderContextResolverTest.class.getSimpleName());
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.preferJacksonOverJsonB", "true");
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, contextParams, JacksonDisableTimeStampProducer.class,
                PortalStatusResource.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Test
    public void testWithoutContextResolver() throws Exception {
        WebTarget target = client.target(generateURL("/status/time"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void testContextResolver() throws Exception {
        WebTarget target = client.target(generateURL("/status/time/register/1"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        MatcherAssert.assertThat("Read entity is not expected",
                entity, containsString("serverTime"));
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JacksonProviderContextResolverTest.class.getSimpleName());
    }
}
