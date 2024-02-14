package org.jboss.resteasy.test.resource.basic;

import java.util.logging.LoggingPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.basic.resource.LogHandler;
import org.jboss.resteasy.test.resource.basic.resource.MultipleEndpointsWarningResource;
import org.jboss.resteasy.utils.PermissionUtil;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-1398
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipleEndpointsWarningTest {
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultipleEndpointsWarningTest.class.getSimpleName());
        war.addClass(LogHandler.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new LoggingPermission("control", "")), "permissions.xml");
        // Test registers it's own LogHandler
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new LoggingPermission("control", "")),
                "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, MultipleEndpointsWarningResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultipleEndpointsWarningTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setUp() throws Exception {
        client = ClientBuilder.newClient();
        client.target(generateURL("/setup")).request().get();
    }

    @AfterAll
    public static void tearDown() {
        client.target(generateURL("/teardown")).request().get();
        client.close();
    }

    @Test
    public void testUnique() throws Exception {
        Response response = client.target(generateURL("/unique/")).request().accept(MediaType.TEXT_PLAIN).get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);

        response = client.target(generateURL("/unique")).request().get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);

        response = client.target(generateURL("/unique")).request().accept(MediaType.TEXT_PLAIN).get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);

        response = client.target(generateURL("/1")).request().get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);
    }

    @Test
    public void testDifferentVerbs() throws Exception {
        Response response = client.target(generateURL("/verbs")).request().accept(MediaType.TEXT_PLAIN).get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);

        response = client.target(generateURL("/verbs")).request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);

        response = client.target(generateURL("/verbs")).request().get();
        Assertions.assertEquals(Long.valueOf(0),
                response.readEntity(long.class),
                "Incorrectly logged " + LogHandler.MESSAGE_CODE);
    }

    @Test
    public void testDuplicate() throws Exception {
        Response response = client.target(generateURL("/duplicate")).request().get();
        Assertions.assertEquals(Long.valueOf(1),
                response.readEntity(long.class),
                LogHandler.MESSAGE_CODE + " should've been logged once");
    }
}
