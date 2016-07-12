package org.jboss.resteasy.test.core.encoding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.core.encoding.resource.EncodedParamsComplexResource;
import org.jboss.resteasy.test.core.encoding.resource.EncodedParamsSimpleResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for special characters in get request
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EncodedParamsTest {

    public static final String ERROR_MESSAGE = "Wrong encoded characters in request";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EncodedParamsTest.class.getSimpleName());
        war.addClass(EncodedParamsTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                EncodedParamsComplexResource.class, EncodedParamsSimpleResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EncodedParamsTest.class.getSimpleName());
    }

    private void basicTest(String path) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL(path)).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Check various location of "?", "%20" characters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncoded() throws Exception {
        basicTest("/encodedParam?hello%20world=5&stuff=hello%20world");
        basicTest("/encodedParam/hello%20world");
        basicTest("/encodedMethod?hello%20world=5&stuff=hello%20world");
        basicTest("/encodedMethod/hello%20world");
    }
}