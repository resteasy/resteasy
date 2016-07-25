package org.jboss.resteasy.test.exception;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperWebRuntimeExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperWebRuntimeExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-595
 */
@RunWith(Arquillian.class)
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
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/test", ExceptionMapperWebRuntimeExceptionTest.class.getSimpleName()));
        Response response = base.request().get();

        Assert.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong headers", response.getHeaders().getFirst("custom"), "header");

        response.close();
        client.close();
    }

}
