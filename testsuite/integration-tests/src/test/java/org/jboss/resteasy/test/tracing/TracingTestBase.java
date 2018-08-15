package org.jboss.resteasy.test.tracing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@RunWith(Arquillian.class)
@RunAsClient
public abstract class TracingTestBase {
    protected static final String TRACING_CONTAINER = "jbossas-manual-tracing";
    protected static final String WAR_BASIC_TRACING_FILE = "war_basic_tracing";
    protected static final String WAR_ON_DEMAND_TRACING_FILE = "war_on_demand_tracing";
    private static final Logger LOG = LogManager.getLogger(TracingTestBase.class);

    @ArquillianResource
    protected ContainerController containerController;

    @ArquillianResource
    protected Deployer deployer;

    static WebArchive war;
    static Client client;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @Before
    public void startContainer() {
        if (!containerController.isStarted(TRACING_CONTAINER)) {
            containerController.start(TRACING_CONTAINER);
        }
        deployer.deploy(WAR_BASIC_TRACING_FILE);
        deployer.deploy(WAR_ON_DEMAND_TRACING_FILE);
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @After
    public void undeployAndStopContainerWithGzipEnabled() {
        if (containerController.isStarted(TRACING_CONTAINER)) {
            deployer.undeploy(WAR_BASIC_TRACING_FILE);
            deployer.undeploy(WAR_ON_DEMAND_TRACING_FILE);
            containerController.stop(TRACING_CONTAINER);
        }
    }

    protected String generateURL(String path, String deploymentName) {
        String fullpath =  PortProviderUtil.generateURL(path, deploymentName,  PortProviderUtil.getHost(), PortProviderUtil.getPort() + 2000);
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

    @Deployment(name = WAR_BASIC_TRACING_FILE, managed = false, testable = false)
    @TargetsContainer(TRACING_CONTAINER)
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(WAR_BASIC_TRACING_FILE);
        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
        params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class, FooLocator.class, Foo.class);

    }


    @Deployment(name = WAR_ON_DEMAND_TRACING_FILE, managed = false, testable = false)
    @TargetsContainer(TRACING_CONTAINER)
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
            String item = entry
                    .getValue()
                    .toString()
                    .split("\\[")[1].split(" ")[0];

            if (results.keySet()
                    .contains(item)) {
                results.put(item.replaceAll(" ", ""), true);
            }
        }
    }


}
