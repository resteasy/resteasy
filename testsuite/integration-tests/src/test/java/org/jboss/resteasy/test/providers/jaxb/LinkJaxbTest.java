package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.jaxb.resource.LinkJaxbCustomer;
import org.jboss.resteasy.test.providers.jaxb.resource.LinkJaxbResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
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
public class LinkJaxbTest {

    private static Logger logger = Logger.getLogger(LinkJaxbTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(LinkJaxbTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, LinkJaxbCustomer.class, LinkJaxbResource.class);
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
        return PortProviderUtil.generateURL(path, LinkJaxbTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for jakarta.ws.rs.core.Link class with Jaxb object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomer() throws Exception {
        Client client = ClientBuilder.newClient();
        String str = client.target(generateURL("")).request().get(String.class);
        logger.info(str);
        client.close();
    }
}
