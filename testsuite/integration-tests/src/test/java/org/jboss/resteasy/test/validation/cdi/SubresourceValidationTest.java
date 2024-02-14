package org.jboss.resteasy.test.validation.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationQueryBeanParam;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationResource;
import org.jboss.resteasy.test.validation.cdi.resource.SubresourceValidationSubResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1103
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SubresourceValidationTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(SubresourceValidationTest.class.getSimpleName())
                .addAsManifestResource(
                        PermissionUtil.createPermissionsXmlAsset(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS),
                        "permissions.xml")
                .addClasses(SubresourceValidationResource.class, SubresourceValidationSubResource.class,
                        SubresourceValidationQueryBeanParam.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, SubresourceValidationResource.class);
    }

    protected Client client;

    @BeforeEach
    public void beforeTest() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void afterTest() {
        client.close();
        client = null;
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
        Invocation.Builder request = client.target(generateURL("/sub/17?limit=abcdef")).request();
        ClientResponse response = (ClientResponse) request.get();
        ViolationReport r = new ViolationReport(response.readEntity(String.class));
        TestUtil.countViolations(r, 0, 0, 2, 0);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
    }

    /**
     * @tpTestDetails Test for validation of returned value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValue() throws Exception {
        Invocation.Builder request = client.target(generateURL("/sub/return/abcd")).request();
        ClientResponse response = (ClientResponse) request.get();
        ViolationReport r = new ViolationReport(response.readEntity(String.class));
        TestUtil.countViolations(r, 0, 0, 0, 1);
        assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }
}
