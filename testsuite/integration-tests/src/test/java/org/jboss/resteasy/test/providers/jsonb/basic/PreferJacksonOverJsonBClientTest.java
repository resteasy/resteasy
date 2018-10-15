package org.jboss.resteasy.test.providers.jsonb.basic;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.PreferJacksonOverJsonBClientResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @tpSubChapter Check that resteasy.preferJacksonOverJsonB property works correctly on client used on server deployment.
 * @tpChapter Integration test
 * @tpSince RESTEasy 3.3
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PreferJacksonOverJsonBClientTest {

    protected static final Logger logger = Logger.getLogger(JsonBindingTest.class.getName());

    static Client client;

    private static final String WAR_WITH_JSONB = "war_with_jsonb";
    private static final String WAR_WITH_JACKSON2 = "war_with_jackson2";

    private static final String JSON_B_DEBUG_LOG = "MessageBodyWriter: org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider";
    private static final String JACKSON_DEBUG_LOG = "MessageBodyWriter: org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider";

    /**
     * Prepare deployment with resteasy.preferJacksonOverJsonB = false
     */
    @Deployment(name = WAR_WITH_JSONB)
    public static Archive<?> deployWithJsonB() {
        return deploy(WAR_WITH_JSONB, false);
    }

    /**
     * Prepare deployment with resteasy.preferJacksonOverJsonB = true
     */
    @Deployment(name = WAR_WITH_JACKSON2)
    public static Archive<?> deployWithoutJsonB() {
        return deploy(WAR_WITH_JACKSON2, true);
    }


    /**
     * Prepare deployment with specific archive name and specific resteasy.preferJacksonOverJsonB value
     */
    public static Archive<?> deploy(String archiveName, Boolean useJackson) {
        WebArchive war = TestUtil.prepareArchive(archiveName);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, useJackson.toString());
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-json-binding-provider services\n"));
        return TestUtil.finishContainerPrepare(war, contextParams, PreferJacksonOverJsonBClientResource.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    /**
     * Enable RESTEasy debug logging
     */
    @BeforeClass
    public static void initLogging() throws Exception {
        OnlineManagementClient client = TestUtil.clientInit();

        // enable RESTEasy debug logging
        TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=ALL)");
        TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:add(level=ALL)");

        client.close();
    }

    /**
     * Disable RESTEasy debug logging
     */
    @AfterClass
    public static void removeLogging() throws Exception {
        OnlineManagementClient client = TestUtil.clientInit();

        // enable RESTEasy debug logging
        TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=INFO)");
        TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:remove()");

        client.close();
    }

    /**
     * @tpTestDetails Set resteasy.preferJacksonOverJsonB=false, check that JsonB is used on client
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void checkWarWithJsonB() throws Exception {
        check(WAR_WITH_JSONB, JSON_B_DEBUG_LOG, JACKSON_DEBUG_LOG);
    }

    /**
     * @tpTestDetails Set resteasy.preferJacksonOverJsonB=true, check that Jackson is used on client
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void checkWarWithJason2() throws Exception {
        check(WAR_WITH_JACKSON2, JACKSON_DEBUG_LOG, JSON_B_DEBUG_LOG);
    }

    /**
     * Perform HTTP call, end-point perform another HTTP call
     * Check log messages
     */
    public void check(String warName, String desiredLog, String undesiredLog) throws Exception {
        LogCounter desiredLogCounter = new LogCounter(desiredLog, false);
        LogCounter undesiredLogCounter = new LogCounter(undesiredLog, false);

        WebTarget target = client.target(PortProviderUtil.generateURL("/call", warName));
        Response r = target.request()
                .header("clientURL", PortProviderUtil.generateURL("/core", warName))
                .get();
        Assert.assertThat("Wrong http response code", r.getStatus(), is(HttpResponseCodes.SC_NO_CONTENT));

        Assert.assertThat("Desired log message not found", desiredLogCounter.count(), greaterThan(0));
        Assert.assertThat("Undesired log message found", undesiredLogCounter.count(), is(0));
    }
}
