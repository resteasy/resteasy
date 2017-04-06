package org.jboss.resteasy.test.client.exception;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.exception.resource.ClientErrorBadMediaTypeHeaderDelegate;
import org.jboss.resteasy.test.client.exception.resource.ClientErrorBadMediaTypeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test client error caused by bad media type
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientErrorBadMediaTypeTest {

    private static Logger logger = Logger.getLogger(ClientErrorBadMediaTypeTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientErrorBadMediaTypeTest.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.addClass(ClientErrorBadMediaTypeHeaderDelegate.class);
        return TestUtil.finishContainerPrepare(war, null, ClientErrorBadMediaTypeResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ClientErrorBadMediaTypeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails This test uses not defined "foo/bar" media type.
     *                Upstream variant of this test was updated by JBEAP-2594.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadContentType() throws Exception {
        // test
        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/")).request().post(Entity.entity("content", "foo/bar"));
        logger.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
        response.close();
        client.close();
    }
}
