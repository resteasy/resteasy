package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.basic.resource.ApplicationInjection;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for injecting of Application
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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
        Assert.assertEquals("Wrong count of initialized applications", 1, ApplicationInjection.instances.size());
        ApplicationInjection app = ApplicationInjection.instances.iterator().next();
        Assert.assertNotNull("Injected application instance should not be null", app.app);
    }
}
