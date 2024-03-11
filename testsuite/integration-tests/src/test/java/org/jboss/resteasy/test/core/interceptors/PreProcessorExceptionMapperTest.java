package org.jboss.resteasy.test.core.interceptors;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.interceptors.resource.PreProcessorExceptionMapperCandlepinException;
import org.jboss.resteasy.test.core.interceptors.resource.PreProcessorExceptionMapperCandlepinUnauthorizedException;
import org.jboss.resteasy.test.core.interceptors.resource.PreProcessorExceptionMapperPreProcessSecurityInterceptor;
import org.jboss.resteasy.test.core.interceptors.resource.PreProcessorExceptionMapperResource;
import org.jboss.resteasy.test.core.interceptors.resource.PreProcessorExceptionMapperRuntimeExceptionMapper;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-433
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PreProcessorExceptionMapperTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GzipTest.class.getSimpleName());
        war.addClass(PreProcessorExceptionMapperCandlepinException.class);
        war.addClass(PreProcessorExceptionMapperCandlepinUnauthorizedException.class);
        return TestUtil.finishContainerPrepare(war, null, PreProcessorExceptionMapperPreProcessSecurityInterceptor.class,
                PreProcessorExceptionMapperRuntimeExceptionMapper.class,
                PreProcessorExceptionMapperResource.class);
    }

    /**
     * @tpTestDetails Generate PreProcessorExceptionMapperCandlepinUnauthorizedException
     * @tpPassCrit SC_PRECONDITION_FAILED (412) HTTP code is excepted
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapper() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(generateURL("/interception", GzipTest.class.getSimpleName())).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
        response.close();
        client.close();
    }

}
