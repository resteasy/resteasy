package org.jboss.resteasy.test.core.servlet.resource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ServletConfigApplication extends Application {
    public static int num_instantiations = 0;

    protected Set<Object> singletons = new HashSet<Object>();
    protected Set<Class<?>> clazzes = new HashSet<Class<?>>();

    public ServletConfigApplication() {
        num_instantiations++;
        singletons.add(new ServletConfigResource());
        clazzes.add(ServletConfigExceptionMapper.class);

    }

    @Override
    public Set<Class<?>> getClasses() {
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public String getHello() {
        return "hello";
    }
}
