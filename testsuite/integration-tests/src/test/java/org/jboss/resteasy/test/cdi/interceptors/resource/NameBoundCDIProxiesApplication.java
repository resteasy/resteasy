package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
@NameBoundProxiesAnnotation
public class NameBoundCDIProxiesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(NameBoundCDIProxiesResource.class);
        classes.add(NameBoundCDIProxiesInterceptor.class);
        return classes;
    }
}
