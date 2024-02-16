package org.jboss.resteasy.test.core.interceptors;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.interceptors.resource.GzipIGZIP;
import org.jboss.resteasy.test.core.interceptors.resource.GzipResource;
import org.jboss.resteasy.test.core.interceptors.resource.Pair;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter GZIP interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Gzip compression tests
 * @tpSince RESTEasy 3.1.0.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GzipSizeTest {

    static Client client;
    protected static final Logger logger = Logger.getLogger(GzipSizeTest.class.getName());

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GzipSizeTest.class.getSimpleName());
        war.addClasses(GzipIGZIP.class, Pair.class);
        // Activate gzip compression on server:
        war.addAsManifestResource("org/jboss/resteasy/test/client/jakarta.ws.rs.ext.Providers",
                "services/jakarta.ws.rs.ext.Providers");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_GZIP_MAX_INPUT, "16");
        return TestUtil.finishContainerPrepare(war, contextParam, GzipResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GzipSizeTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newBuilder() // Activate gzip compression on client:
                .register(AcceptEncodingGZIPFilter.class)
                .register(new GZIPDecodingInterceptor(16))
                .register(GZIPEncodingInterceptor.class)
                .build();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test exceeding configured maximum size on server
     * @tpInfo RESTEASY-1484
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testMaxConfiguredSizeSending() throws Exception {
        byte[] b = new byte[17];
        Variant variant = new Variant(MediaType.APPLICATION_OCTET_STREAM_TYPE, "", "gzip");
        Response response = client.target(generateURL("/big/send")).request().post(Entity.entity(b, variant));
        Assertions.assertEquals(HttpResponseCodes.SC_REQUEST_ENTITY_TOO_LARGE, response.getStatus());
        String message = response.readEntity(String.class);
        Assertions.assertTrue(message.contains("RESTEASY003357"));
        Assertions.assertTrue(message.contains("16"));
    }

    /**
     * @tpTestDetails Test exceeding configured maximum size on client
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testMaxConfiguredSizeReceiving() throws Exception {
        Response response = client.target(generateURL("/big/receive")).request().header(HttpHeaders.ACCEPT_ENCODING, "gzip")
                .post(Entity.entity(17, "text/plain"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        try {
            byte[] b = response.readEntity(byte[].class);
            Assertions.fail("Expecting ProcessingException, not " + b);
        } catch (ProcessingException e) {
            Assertions.assertTrue(e.getMessage().contains("RESTEASY003357"));
            Assertions.assertTrue(e.getMessage().contains("16"));
        } catch (Exception e) {
            Assertions.fail("Expecting ProcessingException, not " + e);
        }
    }
}
