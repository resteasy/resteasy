package org.jboss.resteasy.test.response;

import java.util.HashSet;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.MethodDefaultResource;
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
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Spec requires that HEAD and OPTIONS are handled in a default manner
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MethodDefaultTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(MethodDefaultTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MethodDefaultResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MethodDefaultTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client invokes Head on root resource at /GetTest;
     *                which no request method designated for HEAD;
     *                Verify that corresponding GET Method is invoked.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeadPlain() throws Exception {
        Response response = client.target(generateURL("/GetTest")).request().header("Accept", "text/plain").head();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String header = response.getHeaderString("CTS-HEAD");
        Assertions.assertEquals("text-plain", header, "Wrong CTS-HEAD header");
        response.close();
    }

    /**
     * @tpTestDetails Client invokes HEAD on root resource at /GetTest;
     *                which no request method designated for HEAD;
     *                Verify that corresponding GET Method is invoked.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeadHtml() throws Exception {
        Response response = client.target(generateURL("/GetTest")).request().header("Accept", "text/html").head();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String header = response.getHeaderString("CTS-HEAD");
        Assertions.assertEquals("text-html", header, "Wrong CTS-HEAD header");
        response.close();
    }

    /**
     * @tpTestDetails Client invokes HEAD on sub resource at /GetTest/sub;
     *                which no request method designated for HEAD;
     *                Verify that corresponding GET Method is invoked instead.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeadSubresource() throws Exception {
        Response response = client.target(generateURL("/GetTest/sub")).request().header("Accept", "text/plain").head();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String header = response.getHeaderString("CTS-HEAD");
        Assertions.assertEquals("sub-text-plain", header, "Wrong CTS-HEAD header");
        response.close();
    }

    /**
     * @tpTestDetails If client invokes OPTIONS and there is no request method that exists, verify that an automatic response is
     *                generated
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOptions() throws Exception {

        Response response = client.target(generateURL("/GetTest/sub")).request().options();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String allowedHeader = response.getHeaderString("Allow");
        Assertions.assertNotNull(allowedHeader, "Wrong Allow header");
        String[] allowed = allowedHeader.split(",");
        HashSet<String> set = new HashSet<String>();
        for (String allow : allowed) {
            set.add(allow.trim());
        }

        Assertions.assertTrue(set.contains("GET"), "Wrong Allow header");
        Assertions.assertTrue(set.contains("OPTIONS"), "Wrong Allow header");
        Assertions.assertTrue(set.contains("HEAD"), "Wrong Allow header");
        response.close();
    }

}
