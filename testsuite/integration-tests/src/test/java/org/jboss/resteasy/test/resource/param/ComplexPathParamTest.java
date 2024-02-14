package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamExtensionResource;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamRegressionResteasy145;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamSubRes;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamSubResSecond;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamTrickyResource;
import org.jboss.resteasy.test.resource.param.resource.ComplexPathParamUnlimitedResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for complex path parameters
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ComplexPathParamTest {

    public static final String WRONG_REQUEST_ERROR_MESSAGE = "Wrong content of request";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ComplexPathParamTest.class.getSimpleName());
        war.addClass(ComplexPathParamTest.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.addClass(ComplexPathParamSubRes.class);
        war.addClass(ComplexPathParamSubResSecond.class);
        return TestUtil.finishContainerPrepare(war, null, ComplexPathParamExtensionResource.class,
                ComplexPathParamRegressionResteasy145.class, ComplexPathParamTrickyResource.class,
                ComplexPathParamUnlimitedResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ComplexPathParamTest.class.getSimpleName());
    }

    private void basicTest(String path, String body) {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(generateURL(path)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(body, response.readEntity(String.class),
                "Wrong content of response, url may not be decoded correctly");
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Check special characters and various path combination
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() throws Exception {
        basicTest("/1,2/3/blah4-5ttt", "hello");
        basicTest("/tricky/1,2", "2Groups");
        basicTest("/tricky/h1", "prefixed");
        basicTest("/tricky/1", "hello");
        basicTest("/unlimited/1-on/and/on", "ok");
        basicTest("/repository/workspaces/aaaaaaxvi/wdddd", "sub2");
    }

}
