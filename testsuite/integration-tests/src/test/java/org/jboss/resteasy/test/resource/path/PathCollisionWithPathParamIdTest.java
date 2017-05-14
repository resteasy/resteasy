package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailing;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.resource.path.resource.PathCollisionWithPathParamIdResource;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Request for a resource on a path GET "/list" matches two resources with @Path("/list") and @Path("{id}").
 * See RESTEASY-1559
 * @tpSince RESTEasy 3.1.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PathCollisionWithPathParamIdTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathCollisionWithPathParamIdTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PathCollisionWithPathParamIdResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PathCollisionWithPathParamIdTest.class.getSimpleName());
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Check that correct resource was used, and warning message for multiple resource matching was generated
     * @tpSince RESTEasy 3.1.1
     */
    @Test
    @Category({ExpectedFailing.class, NotForForwardCompatibility.class})
    public void testCollision() {

        LogCounter countPathCollision = new LogCounter("RESTEASY002142: Multiple resource methods match request", false);
        String response = client.target(generateURL("/list")).request().get(String.class);
        Assert.assertThat("Incorrectly logged warning for multiple resource methods match", countPathCollision.count(), is(0));
        Assert.assertThat("Wring resource was chosen","/list", equalToIgnoringCase(response));
    }

}
