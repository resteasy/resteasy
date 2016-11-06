package org.jboss.resteasy.test.exception;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeCustomMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperApplicationRuntimeResource;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperCustomRuntimeException;
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
 * @tpTestCaseDetails Regression test for RESTEASY-421
 */
@RunWith(Arquillian.class)
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
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/test", ExceptionMapperApplicationRuntimeExceptionTest.class.getSimpleName()));
        Response response = base.request().get();

        Assert.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());

        response.close();
        client.close();
    }

}
