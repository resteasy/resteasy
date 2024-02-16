package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericResourceAbstractResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericResourceModel;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericResourceOtherAbstractResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericResourceResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericResourceResource2;
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
 * @tpTestCaseDetails Regression test for RESTEASY-1125. Jaxb message body reader not recognized when using generics in
 *                    complex inheritance structure
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GenericResourceTest {

    String str = "<genericResourceModel></genericResourceModel>";

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(KeepCharsetTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GenericResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, GenericResourceResource.class, GenericResourceResource2.class,
                GenericResourceModel.class, GenericResourceOtherAbstractResource.class, GenericResourceAbstractResource.class);
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
        return PortProviderUtil.generateURL(path, GenericResourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests Jaxb object with resource using inheritance, generics and abstract classes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGenericInheritingResource() throws Exception {
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().post(Entity.entity(str, "application/xml"));
        logger.info("status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String answer = response.readEntity(String.class);
        Assertions.assertEquals("Success!", answer,
                "The response from the server is not the expected one");
        logger.info(answer);
    }

    /**
     * @tpTestDetails Tests Jaxb object with resource using inheritance, generics and abstract classes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGenericResource() throws Exception {
        WebTarget target = client.target(generateURL("/test2"));
        Response response = target.request().post(Entity.entity(str, "application/xml"));
        logger.info("status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String answer = response.readEntity(String.class);
        Assertions.assertEquals("Success!", answer,
                "The response from the server is not the expected one");
        logger.info(answer);
    }
}
