package org.jboss.resteasy.test.core.interceptors;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.core.interceptors.resource.GzipIGZIP;
import org.jboss.resteasy.test.core.interceptors.resource.GzipResource;
import org.jboss.resteasy.test.core.interceptors.resource.Pair;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.net.HttpHeaders;

/**
 * @tpSubChapter GZIP interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Gzip compression tests
 * @tpSince RESTEasy 3.1.0.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GzipSizeTest {

    static ResteasyClient client;
    protected static final Logger logger = LogManager.getLogger(GzipSizeTest.class.getName());

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GzipSizeTest.class.getSimpleName());
        war.addClasses(GzipIGZIP.class, Pair.class);
        // Activate gzip compression on server:
        war.addAsManifestResource("org/jboss/resteasy/test/client/javax.ws.rs.ext.Providers", "services/javax.ws.rs.ext.Providers");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_GZIP_MAX_INPUT, "16");
        return TestUtil.finishContainerPrepare(war, contextParam, GzipResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GzipSizeTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder() // Activate gzip compression on client:
                    .register(AcceptEncodingGZIPFilter.class)
                    .register(new GZIPDecodingInterceptor(16))
                    .register(GZIPEncodingInterceptor.class)
                    .build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test exceeding configured maximum size on server
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testMaxConfiguredSizeSending() throws Exception {
        byte[] b = new byte[17];
        Variant variant = new Variant(MediaType.APPLICATION_OCTET_STREAM_TYPE, "", "gzip");
        Response response = client.target(generateURL("/big/send")).request().post(Entity.entity(b, variant));
        Assert.assertEquals(HttpResponseCodes.SC_REQUEST_ENTITY_TOO_LARGE, response.getStatus());
        String message = response.readEntity(String.class);
        Assert.assertTrue(message.contains("RESTEASY003357"));
        Assert.assertTrue(message.contains("16"));
    }
    
    /**
     * @tpTestDetails Test exceeding configured maximum size on client
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testMaxConfiguredSizeReceiving() throws Exception {
        Response response = client.target(generateURL("/big/receive")).request().header(HttpHeaders.ACCEPT_ENCODING, "gzip").post(Entity.entity(17, "text/plain"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        try
        {
           byte[] b = response.readEntity(byte[].class);
           Assert.fail("Expecting ProcessingException, not " + b);
        }
        catch (ProcessingException e)
        {
           Assert.assertTrue(e.getMessage().contains("RESTEASY003357"));
           Assert.assertTrue(e.getMessage().contains("16"));
        }
        catch (Exception e)
        {
           Assert.fail("Expecting ProcessingException, not " + e);
        }
    }
}
