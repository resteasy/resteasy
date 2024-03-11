package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.asynch.resource.AsyncGenericEntityMessageBodyWriter;
import org.jboss.resteasy.test.asynch.resource.AsyncGenericEntityResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test getting GenericType from return entity.
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncGenericEntityTest {

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AsyncGenericEntityTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null,
                AsyncGenericEntityMessageBodyWriter.class,
                AsyncGenericEntityResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncGenericEntityTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test getting GenericType from return entity.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testCalls() {
        Client client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/test")).request();
        Response response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ok", response.readEntity(String.class));
        response.close();
        client.close();
    }

}
