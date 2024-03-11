package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperWebRuntimeExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperWebRuntimeExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-595
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExceptionMapperWebRuntimeExceptionTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperWebRuntimeExceptionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ExceptionMapperWebRuntimeExceptionMapper.class,
                ExceptionMapperWebRuntimeExceptionResource.class);
    }

    /**
     * @tpTestDetails Check ExceptionMapper for WebApplicationException
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWebAPplicationException() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/test", ExceptionMapperWebRuntimeExceptionTest.class.getSimpleName()));
        Response response = base.request().get();

        Assertions.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        Assertions.assertEquals(response.getHeaders().getFirst("custom"), "header",
                "Wrong headers");

        response.close();
        client.close();
    }

}
