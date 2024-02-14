package org.jboss.resteasy.test.client;

import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.resource.InputStreamResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InputStreamTest extends ClientTestBase {

    @Path("/")
    public interface InputStreamInterface {
        @Path("test")
        @Produces("text/plain")
        @GET
        default InputStream get() {
            return null;
        }

    }

    protected static final Logger logger = Logger.getLogger(InputStreamTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InputStreamTest.class.getSimpleName());
        war.addClass(InputStreamTest.class);
        return TestUtil.finishContainerPrepare(war, null, InputStreamResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request with requested return type of InputStream.
     * @tpPassCrit The response String can be read from returned input stream and matches expected text.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputStream() throws Exception {
        InputStream is = client.target(generateURL("/test")).request().get(InputStream.class);
        byte[] buf = is.readAllBytes();
        String str = new String(buf);
        Assertions.assertEquals("hello world", str,
                "The returned inputStream doesn't contain expexted text");
        logger.info("Text from inputstream: " + str);
        is.close();
    }

    /**
     * @tpTestDetails Client sends GET request with requested return type of InputStream. The request is created
     *                via client proxy
     * @tpPassCrit The response with expected Exception text is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputStreamProxy() throws Exception {
        InputStreamInterface proxy = client.target(generateURL("/")).proxy(InputStreamInterface.class);
        InputStream is = proxy.get();
        byte[] buf = is.readAllBytes();
        String str = new String(buf);
        Assertions.assertEquals("hello world", str,
                "The returned inputStream doesn't contain expexted text");
        is.close();
    }

}
