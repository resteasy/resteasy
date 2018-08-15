package org.jboss.resteasy.test.spring.unit;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Spring
 * @tpChapter Unit test
 * @tpTestCaseDetails  Tests that SpringContextLoader does proper validations and adds an application listener
 * @tpSince RESTEasy 3.0.16
 */
public class SpringContextLoaderTest {

    private SpringContextLoaderSubclass contextLoader;

    @Before
    public void setupEditor() {
        contextLoader = new SpringContextLoaderSubclass();
    }

    /**
     * @tpTestDetails Tests that ResteasyDeployment is required for customizeContext() of SpringContextLoader
     */
    @Test(expected = RuntimeException.class)
    public void testThatDeploymentIsRequired() {
        contextLoader.customizeContext(mockServletContext(null), mockWebApplicationContext());
    }

    /**
     * @tpTestDetails Tests that only one application listener is added for customizeContext() call of SpringContextLoader
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testThatWeAddedAnApplicationListener() {
        StaticWebApplicationContext context = mockWebApplicationContext();
        int numListeners = context.getApplicationListeners().size();
        contextLoader.customizeContext(mockServletContext(someDeployment()), context);
        int numListenersNow = context.getApplicationListeners().size();
        assertEquals("Expected to add exactly one new listener; in fact added " + (numListenersNow - numListeners),
                numListeners + 1, numListenersNow);
    }

    private StaticWebApplicationContext mockWebApplicationContext() {
        return new StaticWebApplicationContext();
    }

    private ServletContext mockServletContext(ResteasyDeployment deployment) {
        MockServletContext context = new MockServletContext();

        if (deployment != null) {
            context.setAttribute(ResteasyDeployment.class.getName(), deployment);
        }

        return context;
    }

    private ResteasyDeployment someDeployment() {
        return new ResteasyDeployment();
    }
}
