package org.jboss.resteasy.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContextParameterInjectionTest {
    private static final String RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES = "resteasy.proxy.implement.all.interfaces";
    private static final Map<String, String> preTestProps = new HashMap<>();

    @BeforeClass
    public static void enableConfigForImplementingAllInterfaces() {
        if (System.getProperties().containsKey(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES)) {
            String value = System.getProperty(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES);
            preTestProps.put(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES, value);
        }
    }

    @AfterClass
    public static void cleanup() {
        if (!preTestProps.containsKey(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES)) {
            System.clearProperty(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES);
        } else {
            preTestProps.forEach(System::setProperty);
        }
        ResteasyProviderFactory.setInstance(null);
    }

    @Test
    public void testInjectedProxyImplementsOnlySpecificInterfaceByDefault() {
        System.clearProperty(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES);
        Object proxy = createProxy();
        assertTrue("Proxy does not implemented expected JAXRS interface", proxy instanceof ContainerRequestFilter);
        assertFalse("Proxy implements non-JAXRS interfaces", proxy instanceof CoolInterface);
    }

    @Test
    public void testInjectedProxyImplementsAllInterfaces() {
        System.setProperty(RESTEASY_PROXY_IMPLEMENT_ALL_INTERFACES, "true");
        Object proxy = createProxy();
        assertTrue("Proxy does not implemented expected JAXRS interface", proxy instanceof ContainerRequestFilter);
        assertTrue("Proxy does not implement all expected interfaces", proxy instanceof CoolInterface);
        assertEquals("cool", ((CoolInterface) proxy).coolMethod());
    }

    private Object createProxy() {
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockServletContext.getAttribute(ResteasyDeployment.class.getName())).thenReturn(null);
        ConfigurationBootstrap configBootstrap = new ListenerBootstrap(mockServletContext);
        ResteasyContext.pushContext(ResteasyDeployment.class, configBootstrap.createDeployment());
        try {
            final Class<ContainerRequestFilter> filterClass = ContainerRequestFilter.class;
            final ContainerRequestFilter coolInstance = new CoolFilter();
            ResteasyProviderFactory mockFactory = mock(ResteasyProviderFactory.class);
            when(mockFactory.getContextData(filterClass, filterClass, null, false)).thenReturn(coolInstance);

            ResteasyProviderFactory.setInstance(mockFactory);

            ContextParameterInjector cpi = new ContextParameterInjector(null, filterClass, filterClass, null, mockFactory);
            return cpi.createProxy();
        } finally {
            ResteasyContext.removeContextDataLevel();
        }
    }

    public interface CoolInterface {
        String coolMethod();
    }

    public class CoolFilter implements ContainerRequestFilter, CoolInterface {

        @Override
        public String coolMethod() {
            return "cool";
        }

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            // do some cool filtering
        }
    }
}
