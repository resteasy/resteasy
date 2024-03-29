package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceNotAResource;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSomeOtherInterface;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSomeOtherResource;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSuperInt;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSuperIntAbstract;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for resource without @Path annotation.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AnnotationInheritanceTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(AnnotationInheritanceTest.class.getSimpleName());
        war.addClasses(AnnotationInheritanceSuperInt.class, AnnotationInheritanceSuperIntAbstract.class,
                AnnotationInheritanceNotAResource.class, AnnotationInheritanceSomeOtherInterface.class);
        return TestUtil.finishContainerPrepare(war, null, AnnotationInheritanceSomeOtherResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AnnotationInheritanceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test basic functionality of test resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSuperclassInterfaceAnnotation() {
        AnnotationInheritanceSomeOtherInterface proxy = client.target(generateURL("/somewhere"))
                .proxy(AnnotationInheritanceSomeOtherInterface.class);
        Assertions.assertEquals("Foo: Fred", proxy.getSuperInt().getFoo());
    }

    /**
     * @tpTestDetails Check wrong resource without @Path annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDetectionOfNonResource() {
        try {
            AnnotationInheritanceSomeOtherInterface proxy = client.target(generateURL("/somewhere"))
                    .proxy(AnnotationInheritanceSomeOtherInterface.class);
            proxy.getFailure().blah();
            Assertions.fail();
        } catch (Exception e) {
            // exception thrown
        }
    }
}
