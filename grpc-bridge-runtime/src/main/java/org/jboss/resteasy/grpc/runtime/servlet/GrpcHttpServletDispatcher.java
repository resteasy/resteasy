package org.jboss.resteasy.grpc.runtime.servlet;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

public class GrpcHttpServletDispatcher extends HttpServlet30Dispatcher {

    private static final long serialVersionUID = -7323100224345687064L;
    private static final Map<String, Servlet> servletMap = new HashMap<String, Servlet>();
    private static final Map<Servlet, ServletContext> servletContextMap = new HashMap<Servlet, ServletContext>();
    private String name;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        name = servletConfig.getServletName();
        addServlet(name, this, servletConfig.getServletContext());
    }

    @Override
    public void destroy() {
        super.destroy();
        removeServlet(name);
    }

    public static void addServlet(String name, Servlet servlet, ServletContext servletContext) {
        servletMap.put(name, servlet);
        servletContextMap.put(servlet, servletContext);
    }

    public static void removeServlet(String name) {
        servletContextMap.remove(servletMap.get(name));
        servletMap.remove(name);
    }

    public static Servlet getServlet(String name) {
        return servletMap.get(name);
    }

    public static ServletContext getServletContext(String servletName) {
        Servlet servlet = servletMap.get(servletName);
        if (servlet == null) {
            return null;
        }
        return servletContextMap.get(servlet);
    }

    public static ServletContext getServletContext(Servlet servlet) {
        return servletContextMap.get(servlet);
    }
}
