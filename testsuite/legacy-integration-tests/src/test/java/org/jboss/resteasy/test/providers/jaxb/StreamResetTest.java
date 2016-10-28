package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Old client test)
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Old client test)
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetPlace;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetResource;
import org.jboss.resteasy.test.providers.jaxb.resource.StreamResetPerson;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.PortProviderUtil;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StreamResetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for JBEAP-2138.  BufferEntity method is called.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJBEAP2138() throws Exception {
        Client client = ClientBuilder.newClient();
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
     * @tpTestDetails Regression test for JBEAP-2138.  BufferEntity method is not called.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJBEAP2138WithoutBufferedEntity() throws Exception {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(generateURL("/test"));
            Response response = target.request().get();

            try {
                response.readEntity(StreamResetPlace.class);
            } catch (Exception e) {
            }

            response.readEntity(StreamResetPerson.class);

            Assert.fail();
        } catch (IllegalStateException e) {
            logger.info("Expected IllegalStateException was thrown");
        }
    }

    /**
     * @tpTestDetails Tests streamReset method of deprecated ClientResponse class. In case exception is thrown during processing
     * response from the server, the stream of the response must be reset before reading it again.
     * @tpPassCrit After exception is thrown the response is parsed correctly with getEntity()
     * @tpInfo RESTEASY-456
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClientRequestResetStream() throws Exception {
        ClientRequest request = new ClientRequest(generateURL("/test"));
        ClientResponse<StreamResetPlace> response = request.get(StreamResetPlace.class);
        boolean exceptionThrown = false;
        try {
            StreamResetPlace place = response.getEntity();

        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue("The expected exception didn't happen", exceptionThrown);

        response.resetStream();
        StreamResetPerson person = response.getEntity(StreamResetPerson.class);
        Assert.assertNotNull("The stream was not correctly reset", person);
        Assert.assertEquals("The response from the server is not the one expected", "bill", person.getName());
    }

}
