package org.jboss.resteasy.plugins.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.servlet.testapp.AppWithAppPath;
import org.jboss.resteasy.plugins.servlet.testapp.AppWithNoAppPath;
import org.jboss.resteasy.plugins.servlet.testapp.Resource1;
import org.junit.jupiter.api.Test;

public class ResteasyServletInitializerTest {

    /**
     * When the web.xml contains a servlet mapping, there is no need for an
     * ApplicationPath annotation on the Application subclass. web.xml looks like:
     *
     * <pre>
     * &lt;servlet&gt;
     *   &lt;servlet-name&gt;org.jboss.resteasy.plugins.servlet.testapp.AppWithNoAppPath&lt;/servlet-name&gt;
     *   &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
     * &lt;/servlet&gt;
     * &lt;servlet-mapping&gt;
     *   &lt;servlet-name&gt;org.jboss.resteasy.plugins.servlet.testapp.AppWithNoAppPath&lt;/servlet-name&gt;
     *   &lt;url-pattern&gt;/myAppMapping/*&lt;/url-pattern&gt;
     * &lt;/servlet-mapping&gt;
     * </pre>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testServletMappingButNoAppPath() throws Exception {
        ResteasyServletInitializer rsi = new ResteasyServletInitializer();
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletRegistration.Dynamic mockReg = mock(ServletRegistration.Dynamic.class);
        when(mockServletContext.getServletRegistration(AppWithNoAppPath.class.getName())).thenReturn(mockReg);
        when(mockServletContext.getServletRegistrations())
                .thenReturn((Map) Collections.singletonMap(AppWithNoAppPath.class.getName(), mockReg));
        when(mockServletContext.addServlet(AppWithNoAppPath.class.getName(), HttpServlet30Dispatcher.class))
                .thenReturn(mockReg);
        when(mockReg.getInitParameter("jakarta.ws.rs.Application")).thenReturn(null);
        when(mockReg.getMappings()).thenReturn(Collections.singleton("/myAppMapping/*"));
        when(mockReg.getName()).thenReturn(AppWithNoAppPath.class.getName());
        when(mockReg.getClassName()).thenReturn(null);

        rsi.onStartup(setOf(AppWithNoAppPath.class, Resource1.class), mockServletContext);
        verify(mockServletContext).getServletRegistration(AppWithNoAppPath.class.getName());
        verify(mockServletContext).getServletRegistrations();
        verify(mockServletContext).addServlet(AppWithNoAppPath.class.getName(), HttpServlet30Dispatcher.class);
        verify(mockReg, times(2)).getMappings();
        verify(mockReg).getInitParameter("jakarta.ws.rs.Application");
        verify(mockReg).getName();
        verify(mockReg).getClassName();
        verify(mockReg).setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES, Resource1.class.getName());
        verify(mockReg).setInitParameter("jakarta.ws.rs.Application", AppWithNoAppPath.class.getName());
        verify(mockReg).setInitParameter("resteasy.servlet.mapping.prefix", "/myAppMapping");
        verifyNoMoreInteractions(mockServletContext);
        verifyNoMoreInteractions(mockReg);
    }

    /**
     * When the web.xml contains a servlet with name only, but no servlet mapping
     * and no ApplicationPath annotation on the Application subclass, then the
     * Application cannot be registered. The web.xml looks like:
     *
     * <pre>
     * &lt;servlet&gt;
     *   &lt;servlet-name&gt;org.jboss.resteasy.plugins.servlet.testapp.AppWithNoAppPath&lt;/servlet-name&gt;
     *   &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
     * &lt;/servlet&gt;
     * </pre>
     */
    @Test
    public void testServletWithNoMappingAndNoAppPath_NotRegistered() throws Exception {
        ResteasyServletInitializer rsi = new ResteasyServletInitializer();
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletRegistration.Dynamic mockReg = mock(ServletRegistration.Dynamic.class);
        when(mockServletContext.getServletRegistration(AppWithNoAppPath.class.getName())).thenReturn(mockReg);
        when(mockServletContext.addServlet(AppWithNoAppPath.class.getName(), HttpServlet30Dispatcher.class))
                .thenReturn(mockReg);
        when(mockReg.getMappings()).thenReturn(Collections.emptySet());
        when(mockReg.getClassName()).thenReturn(null);
        when(mockReg.getName()).thenReturn(AppWithNoAppPath.class.getName());

        rsi.onStartup(setOf(AppWithNoAppPath.class, Resource1.class), mockServletContext);
        verify(mockServletContext).getServletRegistration(AppWithNoAppPath.class.getName());
        verify(mockServletContext).getServletRegistrations();
        // verify(mockServletContext).addServlet(AppWithNoAppPath.class.getName(),
        // HttpServlet30Dispatcher.class);
        verify(mockReg).getMappings();
        verifyNoMoreInteractions(mockServletContext);
        verifyNoMoreInteractions(mockReg);
    }

    /**
     * When the web.xml contains no servlet definition (and thus no servlet mapping)
     * and no ApplicationPath annotation is on the Application subclass, then the
     * Application cannot be registered.
     */
    @Test
    public void testEmptyWebXmlAndAppWithNoAppPath() throws Exception {
        ResteasyServletInitializer rsi = new ResteasyServletInitializer();
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockServletContext.getServletRegistration(AppWithNoAppPath.class.getName())).thenReturn(null);
        when(mockServletContext.getServletRegistrations()).thenReturn(Collections.emptyMap());

        rsi.onStartup(setOf(AppWithNoAppPath.class, Resource1.class), mockServletContext);
        verify(mockServletContext).getServletRegistration(AppWithNoAppPath.class.getName());
        verify(mockServletContext).getServletRegistrations();
        verifyNoMoreInteractions(mockServletContext);
    }

    /**
     * When the Application subclass has an ApplicationPath annotation, then we use
     * those mappings when configuring the dispatcher servlet in the ServletContext.
     */
    @Test
    public void testAppWithAppPathNoWebXml() throws Exception {
        ResteasyServletInitializer rsi = new ResteasyServletInitializer();
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletRegistration.Dynamic mockReg = mock(ServletRegistration.Dynamic.class);
        when(mockServletContext.addServlet(AppWithAppPath.class.getName(), HttpServlet30Dispatcher.class))
                .thenReturn(mockReg);
        when(mockServletContext.getServletRegistration(AppWithAppPath.class.getName())).thenReturn(null);
        when(mockServletContext.getServletRegistrations()).thenReturn(Collections.emptyMap());

        rsi.onStartup(setOf(AppWithAppPath.class, Resource1.class), mockServletContext);
        verify(mockServletContext).getServletRegistration(AppWithAppPath.class.getName());
        verify(mockServletContext).getServletRegistrations();
        verify(mockServletContext).addServlet(AppWithAppPath.class.getName(), HttpServlet30Dispatcher.class);
        verify(mockReg).setLoadOnStartup(1);
        verify(mockReg).setAsyncSupported(true);
        verify(mockReg).addMapping("/myAppPath/*");
        verify(mockReg).setInitParameter("jakarta.ws.rs.Application", AppWithAppPath.class.getName());
        verify(mockReg).setInitParameter("resteasy.servlet.mapping.prefix", "/myAppPath");
        verify(mockReg).setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES, Resource1.class.getName());
        verifyNoMoreInteractions(mockServletContext);
        verifyNoMoreInteractions(mockReg);
    }

    private <T> Set<T> setOf(T... objects) {
        Set<T> set = new HashSet<>();
        for (T arg : objects) {
            set.add(arg);
        }
        return set;
    }
}