package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.test.interceptor.resource.GZIPAnnotationInterface;
import org.jboss.resteasy.test.interceptor.resource.GZIPAnnotationResource;
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
 * @tpSubChapter Interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @GZIP annotation on client (RESTEASY-1265)
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GZIPAnnotationTest {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient()
                .register(AcceptEncodingGZIPFilter.class)
                .register(GZIPEncodingInterceptor.class)
                .register(GZIPDecodingInterceptor.class);
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GZIPAnnotationTest.class.getSimpleName());
        war.addClass(GZIPAnnotationInterface.class);
        war.addAsManifestResource("org/jboss/resteasy/test/client/jakarta.ws.rs.ext.Providers",
                "services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, GZIPAnnotationResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GZIPAnnotationTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test that org.jboss.resteasy.plugins.interceptors.ClientContentEncodingAnnotationFilter
     *                and org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter
     *                are called on client side
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testGZIP() {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
        GZIPAnnotationInterface resource = target.proxy(GZIPAnnotationInterface.class);
        String s = resource.getFoo("test");
        Assertions.assertTrue(s.contains("gzip"));
        Assertions.assertTrue(s.substring(s.indexOf("gzip") + 4).contains("gzip"));
    }
}
