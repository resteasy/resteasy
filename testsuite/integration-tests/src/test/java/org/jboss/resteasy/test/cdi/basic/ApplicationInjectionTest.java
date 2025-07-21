package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.cdi.basic.resource.ApplicationInjection;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for injecting of Application
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class ApplicationInjectionTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, ApplicationInjectionTest.class.getSimpleName() + ".war");
        war.addClass(ApplicationInjection.class);
        return war;
    }

    /**
     * @tpTestDetails Injected application instance should not be null.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAppInjection() throws Exception {
        Assertions.assertEquals(1, ApplicationInjection.instances.size(), "Wrong count of initialized applications");
        ApplicationInjection app = ApplicationInjection.instances.iterator().next();
        Assertions.assertNotNull(app.app, "Injected application instance should not be null");
    }
}
