package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
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

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class OnDemandTracingTest {
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
        return PortProviderUtil.generateURL(path, OnDemandTracingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(OnDemandTracingTest.class.getSimpleName());
        war.addAsResource(new File(BasicTracingTest.class.getClassLoader()
                .getResource("org/jboss/resteasy/test/tracing/logging.properties").getFile()), "logging.properties");
//        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()), true);

        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ON_DEMAND);
        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class);
    }

    @Test
    public void testOnDemand() {
        String url = generateURL("/logger");
        WebTarget base = client.target(url);
        try {

            Response response = base.request().get();
            testTracingEnabled(response, false);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

            response.close();

//            Thread.currentThread().join();

            // enable ON_DEMAND mode
            Response response2 = base.request().header(RESTEasyTracingLogger.HEADER_ACCEPT, "").
                    header(RESTEasyTracingLogger.HEADER_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE).
                    get();
            testTracingEnabled(response2, true);
            response2.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void testTracingEnabled(Response response, boolean flag) {
        boolean hasTracing = false;
        for (Map.Entry entry : response.getStringHeaders().entrySet()) {
            System.out.println("<K, V> ->" + entry);
            if (entry.getKey().toString().startsWith(RESTEasyTracingLogger.HEADER_TRACING_PREFIX)) {
                hasTracing = true;
                break;
            }
        }
        assertEquals(flag, hasTracing);
    }

}
