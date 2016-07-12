package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserCertResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserMembershipResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMultipleUserResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceMatchingMultipleTest {

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceMatchingMultipleTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceMatchingMultipleTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResourceMatchingMultipleUserResource.class, ResourceMatchingMultipleUserCertResource.class, ResourceMatchingMultipleUserMembershipResource.class);
    }

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request for Users resource, with custom id. With 3 Resources available in the
     * application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingUsers() throws Exception {
        String answer = client.target(generateURL("/users/1")).request().get(String.class);
        Assert.assertEquals("The incorrect resource path was chosen", "users/{id} 1", answer);
    }

    /**
     * @tpTestDetails Client sends GET request for Memberships resource, with custom id. With 3 Resources available in the
     * application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingMemberShips() throws Exception {
        String answer = client.target(generateURL("/users/1/memberships")).request().get(String.class);
        Assert.assertEquals("The incorrect resource path was chosen", "users/{id}/memberships 1", answer);
    }

    /**
     * @tpTestDetails Client sends GET request for Certs resource, with custom id. With 3 Resources available in the
     * application, the correct path will be selected.
     * @tpPassCrit The correct Resource path is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatchingCerts() throws Exception {
        String answer = client.target(generateURL("/users/1/certs")).request().get(String.class);
        Assert.assertEquals("The incorrect resource path was chosen", "users/{id}/certs 1", answer);
    }


}
