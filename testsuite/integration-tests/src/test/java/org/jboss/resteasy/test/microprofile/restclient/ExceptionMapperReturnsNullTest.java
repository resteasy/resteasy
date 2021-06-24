package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.microprofile.restclient.resource.HealthCheckData;
import org.jboss.resteasy.test.microprofile.restclient.resource.HealthService;
import org.jboss.resteasy.test.microprofile.restclient.resource.Ignore404ExceptionMapper;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

/**
 * @tpSubChapter MicroProfile rest client
 * @tpChapter Integration tests
 * @tpTestCaseDetails The microprofile-rest-client 2.0 specification describes use of a custom
 * ResponseExceptionMapper implementation and a default ResponseExceptionMapper.
 * There is a scenario in which the default ResponseExceptionMapper has been disabled
 * and the custom ResponseExceptionMapper returns null. This test verifies the proper
 * behavior for this scenario and when the default ResponseExceptionMapper is present.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExceptionMapperReturnsNullTest {

    @Deployment
    public static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperReturnsNullTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null);
    }

    private String generateURL(String path)
    {
        return PortProviderUtil.generateURL(path,
                ExceptionMapperReturnsNullTest.class.getSimpleName());
    }

    @Test
    public void testNoApplicableExceptionMapper() throws Exception
    {
        HealthCheckData data = RestClientBuilder.newBuilder()
                .property("microprofile.rest.client.disable.default.mapper",true)
                .baseUrl(new URL(generateURL("/")))
                .register(new Ignore404ExceptionMapper())
                .build(HealthService.class)
                .getHealthData();
        Assert.assertNull(data);
    }

    @Test
    public void testDefaultExceptionMapper()
    {
        try {
            RestClientBuilder.newBuilder()
                    .baseUrl(new URL(generateURL("/")))
                    .register(new Ignore404ExceptionMapper())
                    .build(HealthService.class)
                    .getHealthData();
            Assert.fail("Exception should have been returned");
        } catch (Exception e) {
            // success exception was returned
        }
    }
}
