package org.jboss.resteasy.test.client;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.InputStreamResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;


/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputStreamTest extends ClientTestBase{

    @Path("/")
    public interface InputStreamInterface {
        @Path("test")
        @Produces("text/plain")
        @GET
        InputStream get();

    }

    protected static final Logger logger = LogManager.getLogger(InputStreamTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InputStreamTest.class.getSimpleName());
        war.addClass(InputStreamTest.class);
        return TestUtil.finishContainerPrepare(war, null, InputStreamResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
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
        byte[] buf = IOUtils.toByteArray(is);
        String str = new String(buf);
        Assert.assertEquals("The returned inputStream doesn't contain expexted text", "hello world", str);
        logger.info("Text from inputstream: " + str);
        is.close();
    }

    /**
     * @tpTestDetails Client sends GET request with requested return type of InputStream. The request is created
     * via client proxy
     * @tpPassCrit The response with expected Exception text is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputStreamProxy() throws Exception {
        InputStreamInterface proxy = client.target(generateURL("/")).proxy(InputStreamInterface.class);
        InputStream is = proxy.get();
        byte[] buf = IOUtils.toByteArray(is);
        String str = new String(buf);
        Assert.assertEquals("The returned inputStream doesn't contain expexted text", "hello world", str);
        is.close();
    }

}
