package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.InvalidMediaTypeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-699
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InvalidMediaTypeTest {

    protected static final Logger logger = Logger.getLogger(InvalidMediaTypeTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InvalidMediaTypeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InvalidMediaTypeResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InvalidMediaTypeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check various wrong media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInvalidMediaTypes() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Invocation.Builder request = client.target(generateURL("/test")).request();

        // Missing slash
        doTest(request, "invalid");

        // Missing type or subtype
        doTest(request, "/");
        doTest(request, "/*");
        doTest(request, "*/");
        doTest(request, "text/");
        doTest(request, "/plain");

        // Illegal white space
        doTest(request, " /*");
        doTest(request, "/* ");
        doTest(request, " /* ");
        doTest(request, "/ *");
        doTest(request, "* /");
        doTest(request, " / *");
        doTest(request, "* / ");
        doTest(request, "* / *");
        doTest(request, " * / *");
        doTest(request, "* / * ");
        doTest(request, "text/ plain");
        doTest(request, "text /plain");
        doTest(request, " text/plain");
        doTest(request, "text/plain ");
        doTest(request, " text/plain ");
        doTest(request, " text / plain ");
        client.close();
    }

    private void doTest(Invocation.Builder request, String mediaType) {
        request.accept(mediaType);
        Response response = request.get();
        logger.info("mediaType: " + mediaType + "");
        logger.info("status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
        response.close();
    }
}
