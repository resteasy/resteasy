package org.jboss.resteasy.test.cdi.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.injection.resource.FinalMethodSuperclass;
import org.jboss.resteasy.test.cdi.injection.resource.NonProxyableProviderResource;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandlerBodyWriter;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalInheritedMethodStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalInheritedMethodStringHandlerBodyWriter;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderOneArgConstructorStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderOneArgConstructorStringHandlerBodyWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-1015
 *                    Test that proxy class is not created for provider class
 *                    that cannot be proxied.
 * @tpSince RESTEasy 3.7
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NonProxyableProviderTest {

    protected static final Logger logger = Logger.getLogger(
            NonProxyableProviderTest.class.getName());

    Client client;

    @Deployment
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "SingleLibrary.jar");
        jar.addClasses(ProviderFinalClassStringHandler.class,
                ProviderFinalClassStringHandlerBodyWriter.class,
                ProviderFinalInheritedMethodStringHandler.class,
                ProviderFinalInheritedMethodStringHandlerBodyWriter.class,
                FinalMethodSuperclass.class,
                ProviderOneArgConstructorStringHandler.class,
                ProviderOneArgConstructorStringHandlerBodyWriter.class);

        WebArchive war = ShrinkWrap.create(WebArchive.class,
                NonProxyableProviderTest.class.getSimpleName() + ".war");
        war.addClass(NonProxyableProviderResource.class);
        war.addAsWebInfResource(
                NonProxyableProviderTest.class.getPackage(),
                "ProviderFinalClass_web.xml", "web.xml");
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsLibrary(jar);
        return war;
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    /**
     * @tpTestDetails Test CDI does not create proxy class for provider bean declared final
     * @tpSince RESTEasy 3.7
     */
    @Test
    public void testFinalProvider() throws Exception {
        test("a");
    }

    /**
     * @tpTestDetails Test CDI does not create proxy class for provider bean with an inherited final method
     * @tpSince RESTEasy 3.7
     */
    @Test
    public void testInheritedFinalMethodProvider() throws Exception {
        test("b");
    }

    /**
     * @tpTestDetails Test CDI does not create proxy class for provider bean without a non-private no-arg constructor
     * @tpSince RESTEasy 3.7
     */
    @Test
    public void testOneArgConstructorProvider() throws Exception {
        test("c");
    }

    private void test(String subpath) {
        String url = PortProviderUtil.generateURL("/new/" + subpath,
                NonProxyableProviderTest.class.getSimpleName());
        WebTarget base = client.target(url);

        Response response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }
}
