package org.jboss.resteasy.test.validation.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationQueryBeanParam;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationResource;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationSubResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1103
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SubresourceValidationTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(SubresourceValidationTest.class.getSimpleName())
                .addClasses(SubresourceValidationResource.class, SubresourceValidationSubResource.class, SubresourceValidationQueryBeanParam.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, SubresourceValidationResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SubresourceValidationTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for subresources
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresource() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/sub/17?limit=abcdef")).request();
        ClientResponse response = (ClientResponse) request.get();
        ViolationReport r = new ViolationReport(response.readEntity(String.class));
        TestUtil.countViolations(r, 0, 0, 0, 2, 0);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
    }

    /**
     * @tpTestDetails Test for validation of returned value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValue() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/sub/return/abcd")).request();
        ClientResponse response = (ClientResponse) request.get();
        ViolationReport r = new ViolationReport(response.readEntity(String.class));
        TestUtil.countViolations(r, 0, 0, 0, 0, 1);
        assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }
}
