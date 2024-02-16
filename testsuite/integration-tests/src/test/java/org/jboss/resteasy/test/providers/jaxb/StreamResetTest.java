package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetPerson;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetPlace;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetResource;
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
public class StreamResetTest {

    private final Logger logger = Logger.getLogger(StreamResetTest.class);

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(StreamResetTest.class.getSimpleName());
        war.addClass(StreamResetTest.class);
        return TestUtil.finishContainerPrepare(war, null, StreamResetPlace.class, StreamResetPerson.class,
                StreamResetResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StreamResetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for JBEAP-2138. BufferEntity method is called.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJBEAP2138() throws Exception {
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().get();

        response.bufferEntity();

        try {
            response.readEntity(StreamResetPlace.class);
        } catch (Exception e) {
        }

        response.readEntity(StreamResetPerson.class);
    }

    /**
     * @tpTestDetails Regression test for JBEAP-2138. BufferEntity method is not called.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJBEAP2138WithoutBufferedEntity() throws Exception {
        try {
            WebTarget target = client.target(generateURL("/test"));
            Response response = target.request().get();

            try {
                response.readEntity(StreamResetPlace.class);
            } catch (Exception e) {
            }

            response.readEntity(StreamResetPerson.class);

            Assertions.fail();
        } catch (IllegalStateException e) {
            logger.info("Expected IllegalStateException was thrown");
        }
    }

}
