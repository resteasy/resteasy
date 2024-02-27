package org.jboss.resteasy.test.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.setup.AllowTraceMethodSetupTask;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.TraceResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(AllowTraceMethodSetupTask.class)
public class TraceTest extends ClientTestBase {

    protected static final Logger logger = Logger.getLogger(TraceTest.class.getName());
    private static Client client;

    @HttpMethod("TRACE")
    @Target(value = ElementType.METHOD)
    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface TRACE {
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TraceTest.class.getSimpleName());
        war.addClass(TraceTest.class);
        return TestUtil.finishContainerPrepare(war, null, TraceResource.class);
    }

    @Before
    public void init() {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends request for custom defined http method 'TRACE'
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void TraceTest() {
        Response response = client.target(generateURL("/resource/trace")).request().trace(Response.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }
}
