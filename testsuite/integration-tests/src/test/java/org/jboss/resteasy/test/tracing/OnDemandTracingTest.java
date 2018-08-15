package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OnDemandTracingTest extends TracingTestBase {

    private static final Logger LOG = LogManager.getLogger(OnDemandTracingTest.class);

    @Test
    @OperateOnDeployment(WAR_ON_DEMAND_TRACING_FILE)
    public void testOnDemand() {
        String url = generateURL("/logger", WAR_ON_DEMAND_TRACING_FILE);
        WebTarget base = client.target(url);
        try {

            Response response = base.request().get();
            testTracingEnabled(response, false);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

            response.close();

//            Thread.currentThread().join();

            // enable ON_DEMAND mode
            Response response2 = base.request().header(RESTEasyTracing.HEADER_ACCEPT, "").
                    header(RESTEasyTracing.HEADER_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE).
                    get();
            testTracingEnabled(response2, true);
            response2.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @OperateOnDeployment(WAR_ON_DEMAND_TRACING_FILE)
    public void testPresencesOfServerTracingEvents() {
        String url = generateURL("/locator/foo", WAR_ON_DEMAND_TRACING_FILE);

        WebTarget base = client.target(url);

        try {
            Response response = base.request()
                    .header(RESTEasyTracing.HEADER_ACCEPT, "").
                            header(RESTEasyTracing.HEADER_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE)
                    .get();
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


    private void testTracingEnabled(Response response, boolean flag) {
        boolean hasTracing = false;
        for (Map.Entry entry : response.getStringHeaders().entrySet()) {
            if (entry.getKey().toString().startsWith(RESTEasyTracing.HEADER_TRACING_PREFIX)) {
            LOG.info("<K, V> ->" + entry);
                hasTracing = true;
                break;
            }
        }
        assertEquals(flag, hasTracing);
    }

}
