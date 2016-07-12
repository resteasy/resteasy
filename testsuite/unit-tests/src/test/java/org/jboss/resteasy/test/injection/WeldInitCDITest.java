package org.jboss.resteasy.test.injection;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @tpSubChapter Injection tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for weld initialize method
 * @tpSince RESTEasy 3.0.16
 */
public class WeldInitCDITest {
    /**
     * @tpTestDetails Initialized weld should enable lookupBeanManagerCDIUtil method to work correctly.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInitWeld() {
        Weld weld = new Weld();
        WeldContainer weldContainer = weld.initialize(); // next assert fails without initialized weld
        BeanManager bm = CdiInjectorFactory.lookupBeanManagerCDIUtil();
        Assert.assertNotNull("Bean manager was not initialized successfully", bm);
        weld.shutdown();
    }
}
