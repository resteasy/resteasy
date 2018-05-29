package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.resource.basic.ConstructedInjectionTest;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
public class BasicTracingTest {
    static WebArchive war;
    static Client client;


    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, BasicTracingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(BasicTracingTest.class.getSimpleName());
        war.addAsResource(new File(BasicTracingTest.class.getClassLoader().getResource("org/jboss/resteasy/test/tracing/logging.properties").getFile()), "logging.properties");
        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
        params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class);
    }

    @Test
    public void testBasic() throws InterruptedException {
//        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()), true);
//        Thread.currentThread().join();
        String url = generateURL("/logger");
//        System.out.println("::: " + url);
//        Thread.currentThread().join();
        WebTarget base = client.target(url);
        try {
            Response response = base.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            boolean hasTracing = false;
            for (Map.Entry entry : response.getStringHeaders().entrySet()) {
                System.out.println("<K, V> ->" + entry);
                if (entry.getKey().toString().startsWith(RESTEasyTracingLogger.HEADER_TRACING_PREFIX)) {
                    hasTracing = true;
                    break;
                }
            }
            assertTrue(hasTracing);
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

