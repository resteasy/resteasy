package org.jboss.resteasy.test.tracing;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule("org.jboss.resteasy.resteasy-tracing-api")
public abstract class TracingTestBase {
    protected static final String WAR_BASIC_TRACING_FILE = "war_basic_tracing";
    protected static final String WAR_ON_DEMAND_TRACING_FILE = "war_on_demand_tracing";
    private static final Logger LOG = Logger.getLogger(TracingTestBase.class);

    static WebArchive war;
    static Client client;

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() {
        client.close();
    }

    protected String generateURL(String path, String deploymentName) {
        String fullpath = PortProviderUtil.generateURL(path, deploymentName, PortProviderUtil.getHost(),
                PortProviderUtil.getPort());
        LOG.info(":::PATH: " + fullpath);
        return fullpath;
    }

    protected void putTestEvents(Map<String, Boolean> results) {
        results.put("PRE-MATCH", false);
        results.put("REQ-FILTER", false);
        results.put("RESP-FILTER", false);
        results.put("MATCH", false);
        results.put("INVOKE", false);
        results.put("FINISHED", false);

        // verbose events
        results.put("MBW", false);
        results.put("WI", false);
    }

    @Deployment(name = WAR_BASIC_TRACING_FILE, testable = false)
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(WAR_BASIC_TRACING_FILE);
        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
        params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD,
                ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class, FooLocator.class, Foo.class);

    }

    @Deployment(name = WAR_ON_DEMAND_TRACING_FILE, testable = false)
    public static Archive<?> createDeployment2() {
        war = TestUtil.prepareArchive(WAR_ON_DEMAND_TRACING_FILE);

        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ON_DEMAND);
        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class, FooLocator.class, Foo.class);
    }

    protected void verifyResults(Response response, Map<String, Boolean> results) {
        for (Map.Entry entry : response.getStringHeaders().entrySet()) {
            LOG.info("<K, V> ->" + entry);

            try {
                String item = entry
                        .getValue()
                        .toString()
                        .split("\\[")[1].split(" ")[1];

                if (results.keySet()
                        .contains(item)) {
                    results.put(item.replaceAll(" ", ""), true);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // irrelevant response headers
            }
        }
    }
}
