package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeCustomMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeResource;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeException;
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
public class ExceptionMapperApplicationRuntimeExceptionTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperApplicationRuntimeExceptionTest.class.getSimpleName());
        war.addClass(ExceptionMapperCustomRuntimeException.class);
        return TestUtil.finishContainerPrepare(war, null, ExceptionMapperApplicationRuntimeCustomMapper.class,
                ExceptionMapperApplicationRuntimeMapper.class, ExceptionMapperApplicationRuntimeResource.class);
    }

    /**
     * @tpTestDetails Check ExceptionMapper for ApplicationException
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapper() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(
                PortProviderUtil.generateURL("/test", ExceptionMapperApplicationRuntimeExceptionTest.class.getSimpleName()));
        Response response = base.request().get();

        Assertions.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());

        response.close();
        client.close();
    }

}
