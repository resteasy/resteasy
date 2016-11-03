package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilter;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilterResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseFilterTest {

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseFilterTest.class.getSimpleName());
        war.addClasses(CollectionProviderTest.class);
        return TestUtil.finishContainerPrepare(war, null, ResponseFilterResource.class, ResponseFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseFilterTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() {
        client.close();
    }


    /**
     * @tpTestDetails Client sends POST requests for each possible Response status code as the entity. The Response is
     * set up for the response code of the request. Response is then processed by ResponseFilter which sets response code
     * to 200 (OK) and puts the original response code into the body of the response.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and response contains the original code from the
     * request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStatus() {
        for (Response.Status status : Response.Status.values()) {
            String content = String.valueOf(status.getStatusCode());
            Response response = client.target(generateURL("/resource/getstatus")).request().post(Entity.text(content));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals("The entity doesn't contain the original http code of the request"
                    , content, response.readEntity(String.class));
            response.close();
        }

    }

    /**
     * @tpTestDetails Client sends POST requests for each possible Response status code as the entity. The Response is
     * set up for the response code of the request. Response is then processed by ResponseFilter which sets response code
     * to 200 (OK) if the statusInfo() method of the responseContext returns non-null result.
     * Then it puts the original response code into the body of the response. If the statusInfo() method returns null result
     * the entity of the response is set up to null.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and response contains the original code from the
     * request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStatusInfo() {
        for (Response.Status status : Response.Status.values()) {
            String content = String.valueOf(status.getStatusCode());
            Response response = client.target(generateURL("/resource/getstatusinfo")).request().post(Entity.text(content));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals("The entity doesn't contain the original http code of the request", content,
                    response.readEntity(String.class));
            response.close();
        }

    }

    /**
     * @tpTestDetails Client sends POST requests with the text entity containing information abou type. Response filter
     * uses getEntityType() method of the responseContext to get information about type of the entity in the Response.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and response contains the original code from the
     * request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEntityType() {
        String content = "string";
        Response response = client.target(generateURL("/resource/getentitytype")).request().post(Entity.text(content));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The entity doesn't contain the original entity type", String.class.getName()
                , response.readEntity(String.class));
        response.close();

    }


}
