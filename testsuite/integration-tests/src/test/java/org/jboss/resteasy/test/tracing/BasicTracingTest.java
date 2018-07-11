package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


public class BasicTracingTest extends TracingTestBase {

    @Test
    @OperateOnDeployment(WAR_BASIC_TRACING_FILE)
    public void testPresencesOfServerTracingEvents() {
        String url = generateURL("/locator/foo", WAR_BASIC_TRACING_FILE);

        WebTarget base = client.target(url);

        try {
            Response response = base.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

            Map<String, Boolean> results = new HashMap<String, Boolean>();

            putTestEvents(results);

            verifyResults(response, results);

            for (String k : results.keySet()) {
                assertTrue(k + ": " + results.get(k), results.get(k));
            }

            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @OperateOnDeployment(WAR_BASIC_TRACING_FILE)
    public void testBasic() throws InterruptedException {
//        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()), true);
//        Thread.currentThread().join();

        String url = generateURL("/logger", WAR_BASIC_TRACING_FILE);
//        System    .out.println("::: " + url);
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

