package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeCustomMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeException;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeResource;
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
 * @tpTestCaseDetails Regression test for RESTEASY-421
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExceptionMapperCustomRuntimeExceptionTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperCustomRuntimeExceptionTest.class.getSimpleName());
        war.addClass(ExceptionMapperCustomRuntimeException.class);
        return TestUtil.finishContainerPrepare(war, null, ExceptionMapperCustomRuntimeCustomMapper.class,
                ExceptionMapperCustomRuntimeResource.class, ExceptionMapperCustomRuntimeMapper.class);
    }

    /**
     * @tpTestDetails Check ExceptionMapper for Custom RuntimeException. Check the response contains headers and entity
     *                from custom exception mapper. Using Resteasy client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapperWithResteasyClient() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/test", ExceptionMapperCustomRuntimeExceptionTest.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        Assertions.assertEquals(response.getHeaders().getFirst("custom"), "header", "Wrong headers");
        Assertions.assertEquals("My custom message", response.readEntity(String.class),
                "The response doesn't contain the entity from custom exception mapper");

        response.close();
        client.close();
    }

}
