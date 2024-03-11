package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassPerson;
import org.jboss.resteasy.test.providers.jaxb.resource.ExceptionMapperJaxbMapper;
import org.jboss.resteasy.test.providers.jaxb.resource.ExceptionMapperJaxbResource;
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
public class ExceptionMapperJaxbTest {

    private static Logger logger = Logger.getLogger(ExceptionMapperJaxbTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperJaxbTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ExceptionMapperJaxbMapper.class, ExceptionMapperJaxbResource.class,
                AbstractJaxbClassPerson.class);
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
        return PortProviderUtil.generateURL(path, ExceptionMapperJaxbTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for custom JAXBUnmarshalException excetion mapper
     * @tpInfo RESTEASY-519
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFailure() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().post(Entity.entity("<person", "application/xml"));
        Assertions.assertEquals(400, response.getStatus());
        String output = response.readEntity(String.class);
        logger.info(output);
    }

}
