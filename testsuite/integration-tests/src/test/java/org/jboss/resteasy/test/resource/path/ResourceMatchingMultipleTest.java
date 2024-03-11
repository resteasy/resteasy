package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserCertResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserMembershipResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceMatchingMultipleTest {

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceMatchingMultipleTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceMatchingMultipleTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResourceMatchingMultipleUserResource.class,
                ResourceMatchingMultipleUserCertResource.class, ResourceMatchingMultipleUserMembershipResource.class);
    }

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request for Users resource, with custom id. With 3 Resources available in the
     *                application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingUsers() throws Exception {
        String answer = client.target(generateURL("/users/1")).request().get(String.class);
        Assertions.assertEquals("users/{id} 1", answer,
                "The incorrect resource path was chosen");
    }

    /**
     * @tpTestDetails Client sends GET request for Memberships resource, with custom id. With 3 Resources available in the
     *                application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingMemberShips() throws Exception {
        String answer = client.target(generateURL("/users/1/memberships")).request().get(String.class);
        Assertions.assertEquals("users/{id}/memberships 1", answer,
                "The incorrect resource path was chosen");
    }

    /**
     * @tpTestDetails Client sends GET request for Certs resource, with custom id. With 3 Resources available in the
     *                application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingCerts() throws Exception {
        String answer = client.target(generateURL("/users/1/certs")).request().get(String.class);
        Assertions.assertEquals("users/{id}/certs 1", answer,
                "The incorrect resource path was chosen");
    }

}
