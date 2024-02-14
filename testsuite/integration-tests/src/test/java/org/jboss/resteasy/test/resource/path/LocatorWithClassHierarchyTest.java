package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyLocatorResource;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyMiddleResource;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyParamEntityPrototype;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyParamEntityWithConstructor;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyPathParamResource;
import org.jboss.resteasy.test.resource.path.resource.LocatorWithClassHierarchyPathSegmentImpl;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class LocatorWithClassHierarchyTest {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(LocatorWithClassHierarchyTest.class.getSimpleName());
        war.addClasses(LocatorWithClassHierarchyPathSegmentImpl.class, LocatorWithClassHierarchyMiddleResource.class,
                LocatorWithClassHierarchyPathParamResource.class, LocatorWithClassHierarchyParamEntityWithConstructor.class,
                LocatorWithClassHierarchyParamEntityPrototype.class);
        return TestUtil.finishContainerPrepare(war, null, LocatorWithClassHierarchyLocatorResource.class);
    }

    /**
     * @tpTestDetails Client sends POST request with null entity for the resource Locator, which creates the targeted
     *                resource object.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocatorWithSubWithPathAnnotation() {
        Response response = client.target(
                PortProviderUtil.generateURL("/resource/locator/ParamEntityWithConstructor/ParamEntityWithConstructor=JAXRS",
                        LocatorWithClassHierarchyTest.class.getSimpleName()))
                .request().post(null);
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }
}
