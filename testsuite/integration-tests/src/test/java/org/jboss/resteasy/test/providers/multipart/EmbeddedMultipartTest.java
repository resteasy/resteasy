package org.jboss.resteasy.test.providers.multipart;

import java.lang.reflect.ReflectPermission;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.EmbeddedMultipartCustomer;
import org.jboss.resteasy.test.providers.multipart.resource.EmbeddedMultipartResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-929
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EmbeddedMultipartTest {

    protected static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EmbeddedMultipartTest.class.getSimpleName());
        war.addClass(EmbeddedMultipartCustomer.class);
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, EmbeddedMultipartResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EmbeddedMultipartTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test embedded part of multipart message
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmbedded() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/embedded"));
        EmbeddedMultipartCustomer customer = new EmbeddedMultipartCustomer("bill");
        MultipartOutput innerPart = new MultipartOutput();
        innerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
        MultipartOutput outerPart = new MultipartOutput();
        outerPart.addPart(innerPart, MULTIPART_MIXED);
        Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
        String response = target.request().post(entity, String.class);
        Assertions.assertEquals("bill", response, "Wrong content of response");
        client.close();
    }

    /**
     * @tpTestDetails Test complete multipart message
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomer() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/customer"));
        EmbeddedMultipartCustomer customer = new EmbeddedMultipartCustomer("bill");
        MultipartOutput outerPart = new MultipartOutput();
        outerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
        Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
        String response = target.request().post(entity, String.class);
        Assertions.assertEquals("bill", response, "Wrong content of response");
        client.close();
    }

    /**
     * @tpTestDetails Test exception in embedded multipart message
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInvalid() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        try {
            ResteasyWebTarget target = client.target(generateURL("/invalid"));
            EmbeddedMultipartCustomer customer = new EmbeddedMultipartCustomer("bill");
            MultipartOutput outerPart = new MultipartOutput();
            outerPart.addPart(customer, MediaType.APPLICATION_XML_TYPE);
            Entity<MultipartOutput> entity = Entity.entity(outerPart, MULTIPART_MIXED);
            target.request().post(entity, String.class);
            Assertions.fail("Exception is expected");
        } catch (InternalServerErrorException e) {
            Response response = e.getResponse();
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR,
                    response.getStatus(), "Wrong type of exception");
        } finally {
            client.close();
        }
    }
}
