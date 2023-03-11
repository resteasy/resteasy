package org.jboss.resteasy.test.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.response.resource.ResponseHeaderExceptionMapper;
import org.jboss.resteasy.test.response.resource.ResponseHeaderExceptionMapperRuntimeException;
import org.jboss.resteasy.test.response.resource.ResponseHeaderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check that HEADS can replace existing text with new specified text
 * @tpSince RESTEasy 3.0.23
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseHeaderTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ResponseHeaderTest.class.getSimpleName());
        war.addClass(ResponseHeaderExceptionMapperRuntimeException.class);
        return TestUtil.finishContainerPrepare(war, null,
                ResponseHeaderExceptionMapper.class,
                ResponseHeaderResource.class);
    }

    /**
     * @tpTestDetails Check the response headers contain the changes made via the
     *                from custom exception mapper. Using Resteasy client.
     * @tpSince RESTEasy 3.0.23
     */
    @Test
    public void testMapperWithResteasyClient() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(PortProviderUtil.generateURL("/test",
                ResponseHeaderTest.class.getSimpleName()));
        Response response = base.request().get();
        MultivaluedMap<String, Object> headers = response.getHeaders();
        List<Object> objs = headers.get("Server");

        if (objs instanceof ArrayList) {
            if (objs.size() != 2) {
                Assert.fail("2 array objects expected " + objs.size() + " were returned");
            }

            Assert.assertEquals("Wrong headers",
                    (String) objs.get(0) + "," + (String) objs.get(1),
                    "WILDFLY/TEN.Full,AndOtherStuff");
        } else {
            Assert.fail("Expected header data value to be of type ArrayList.  It was not.");
        }

        response.close();
        client.close();
    }
}
