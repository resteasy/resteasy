package org.jboss.resteasy.test.rx.rxjava2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.rx.rxjava2.resource.NoStreamRx2Resource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NoStreamRx2Test {
    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(NoStreamRx2Test.class.getSimpleName())
                .addAsManifestResource(
                        // Required until WFLY-17051 is resolved
                        DeploymentDescriptors
                                .createPermissionsXmlAsset(DeploymentDescriptors.addModuleFilePermission("org.eclipse.yasson")),
                        "permissions.xml");
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
        return TestUtil.finishContainerPrepare(war, null, NoStreamRx2Resource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NoStreamRx2Test.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testSingle() throws InterruptedException {
        String data = client.target(generateURL("/single")).request().get(String.class);
        Assertions.assertEquals("got it", data);

        String[] data2 = client.target(generateURL("/observable")).request().get(String[].class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data2);

        data2 = client.target(generateURL("/flowable")).request().get(String[].class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data2);

        data = client.target(generateURL("/context/single")).request().get(String.class);
        Assertions.assertEquals("got it", data);

        data2 = client.target(generateURL("/context/observable")).request().get(String[].class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data2);

        data2 = client.target(generateURL("/context/flowable")).request().get(String[].class);
        assertArrayEquals(new String[] { "one", "two" }, data2);
    }
}
