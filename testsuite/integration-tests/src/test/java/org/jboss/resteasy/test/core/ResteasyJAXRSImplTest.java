package org.jboss.resteasy.test.core;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxrs implementation
 * @tpChapter Integration tests
 * @tpTestCaseDetails RESTEASY-1531
 * @tpSince RESTEasy 3.1.0
 */
@ExtendWith(ArquillianExtension.class)
public class ResteasyJAXRSImplTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResteasyJAXRSImplTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private ResteasyProviderFactory factory;

    @BeforeEach
    public void setup() {
        // Create an instance and set it as the singleton to use
        factory = ResteasyProviderFactory.newInstance();
        ResteasyProviderFactory.setInstance(factory);
        RegisterBuiltin.register(factory);
    }

    @AfterEach
    public void cleanup() {
        // Clear the singleton
        ResteasyProviderFactory.clearInstanceIfEqual(factory);
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    @RunAsClient
    public void testClientBuilder() throws Exception {
        testClientBuilderNewBuilder();
    }

    /**
     * @tpTestDetails Tests that ResteasyClientBuilder implementation corresponds to JAXRS spec ClientBuilder. Tested client
     *                is bundled in the server.
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testInContainerClientBuilder() throws Exception {
        testClientBuilderNewBuilder();
    }

    /**
     * @tpTestDetails Tests RuntimeDelegate instance implementation with ResteasyProviderFactory
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    @RunAsClient
    public void testRuntimeDelegate() throws Exception {
        testRuntimeDelegateGetInstance();
        testResteasyProviderFactoryGetInstance();
        testResteasyProviderFactoryNewInstance();
    }

    /**
     * @tpTestDetails Tests RuntimeDelegate instance implementation with ResteasyProviderFactory in the container.
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testInContainerRuntimeDelegate() throws Exception {
        testRuntimeDelegateGetInstance();
        testResteasyProviderFactoryGetInstance();
        testResteasyProviderFactoryNewInstance();
    }

    private void testClientBuilderNewBuilder() {
        ClientBuilder client = ClientBuilder.newBuilder();
        Assertions.assertTrue(client instanceof ResteasyClientBuilder);
    }

    private void testRuntimeDelegateGetInstance() {
        RuntimeDelegate.setInstance(null);
        RuntimeDelegate rd = RuntimeDelegate.getInstance();
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rd.getClass()));
        RuntimeDelegate.setInstance(null);
    }

    private void testResteasyProviderFactoryGetInstance() {
        ResteasyProviderFactory.setInstance(null);
        ResteasyProviderFactory rpf = ResteasyProviderFactory.getInstance();
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpf.getClass()));
        Assertions.assertEquals(rpf, ResteasyProviderFactory.getInstance());
        ResteasyProviderFactory.setInstance(null);
        ResteasyProviderFactory rpf2 = ResteasyProviderFactory.getInstance();
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpf2.getClass()));
        Assertions.assertNotEquals(rpf, rpf2);
        ResteasyProviderFactory.setInstance(null);
    }

    private void testResteasyProviderFactoryNewInstance() {
        ResteasyProviderFactory.setInstance(null);
        ResteasyProviderFactory rpf = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(rpf);
        ResteasyProviderFactory rpf2 = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(rpf2);
        ResteasyProviderFactory rpf3 = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(rpf3);
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpf.getClass()));
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpf2.getClass()));
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpf3.getClass()));
        Assertions.assertNotEquals(rpf, rpf2);
        Assertions.assertNotEquals(rpf, rpf3);
        Assertions.assertNotEquals(rpf2, rpf3);

        ResteasyProviderFactory rpfGI = ResteasyProviderFactory.getInstance();
        Assertions.assertTrue(ResteasyProviderFactory.class.isAssignableFrom(rpfGI.getClass()));
        Assertions.assertNotEquals(rpfGI, rpf3);
    }

}
