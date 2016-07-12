package org.jboss.resteasy.test.spring.unit;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
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
     * @tpTestDetails Tests that ProviderFactory is required for customizeContext() of SpringContextLoader
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = RuntimeException.class)
    public void testThatProviderFactoryIsRequired() {
        contextLoader.customizeContext(
                mockServletContext(null, someRegistry(), someDispatcher()),
                mockWebApplicationContext());
    }

    /**
     * @tpTestDetails Tests that Registry is required for customizeContext() of SpringContextLoader
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = RuntimeException.class)
    public void testThatRegistryIsRequired() {
        contextLoader.customizeContext(
                mockServletContext(someProviderFactory(), null, someDispatcher()),
                mockWebApplicationContext());
    }

    /**
     * @tpTestDetails Tests that Dispatcher is required for customizeContext() of SpringContextLoader
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = RuntimeException.class)
    public void testThatDispatcherIsRequired() {
        contextLoader.customizeContext(
                mockServletContext(someProviderFactory(), someRegistry(), null),
                mockWebApplicationContext());
    }

    /**
     * @tpTestDetails Tests that only one application listener is added for customizeContext() call of SpringContextLoader
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testThatWeAddedAnApplicationListener() {
        StaticWebApplicationContext context = mockWebApplicationContext();
        int numListeners = context.getApplicationListeners().size();
        contextLoader.customizeContext(
                mockServletContext(someProviderFactory(), someRegistry(), someDispatcher()),
                context);
        int numListenersNow = context.getApplicationListeners().size();
        assertEquals("Expected to add exactly one new listener; in fact added " + (numListenersNow - numListeners),
                numListeners + 1, numListenersNow);
    }

    private StaticWebApplicationContext mockWebApplicationContext() {
        return new StaticWebApplicationContext();
    }

    private ServletContext mockServletContext(
            ResteasyProviderFactory providerFactory,
            Registry registry,
            Dispatcher dispatcher) {
        MockServletContext context = new MockServletContext();

        if (providerFactory != null) {
            context.setAttribute(ResteasyProviderFactory.class.getName(), providerFactory);
        }

        if (registry != null) {
            context.setAttribute(Registry.class.getName(), registry);
        }

        if (dispatcher != null) {
            context.setAttribute(Dispatcher.class.getName(), dispatcher);
        }

        return context;
    }

    private Registry someRegistry() {
        return new ResourceMethodRegistry(someProviderFactory());
    }

    private ResteasyProviderFactory someProviderFactory() {
        return new ResteasyProviderFactory();
    }

    private Dispatcher someDispatcher() {
        return MockDispatcherFactory.createDispatcher();
    }
}
