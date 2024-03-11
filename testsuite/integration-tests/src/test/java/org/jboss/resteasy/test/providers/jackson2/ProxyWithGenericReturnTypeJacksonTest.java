package org.jboss.resteasy.test.providers.jackson2;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonAbstractParent;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonResource;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonSubResourceIntf;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonSubResourceSubIntf;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonType1;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonType2;
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
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyWithGenericReturnTypeJacksonTest {

    protected static final Logger logger = Logger.getLogger(ProxyWithGenericReturnTypeJacksonTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
        war.addClass(Jackson2Test.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyWithGenericReturnTypeJacksonAbstractParent.class,
                ProxyWithGenericReturnTypeJacksonResource.class, ProxyWithGenericReturnTypeJacksonSubResourceIntf.class,
                ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class, ProxyWithGenericReturnTypeJacksonType1.class,
                ProxyWithGenericReturnTypeJacksonType2.class);
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
        return PortProviderUtil.generateURL(path, ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests usage of proxied subresource
     * @tpPassCrit The resource returns Success response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxyWithGenericReturnType() throws Exception {
        WebTarget target = client.target(generateURL("/test/one/"));
        logger.info("Sending request");
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info("Received response: " + entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(entity.contains("type"), "Type property is missing.");
        response.close();

        target = client.target(generateURL("/test/list/"));
        logger.info("Sending request");
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received response: " + entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(entity.contains("type"), "Type property is missing.");
        response.close();
    }
}
