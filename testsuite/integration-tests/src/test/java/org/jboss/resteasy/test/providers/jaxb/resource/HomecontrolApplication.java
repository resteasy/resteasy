package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/service")
public class HomecontrolApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(HomecontrolService.class);
        return result;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> result = new HashSet<>();
        result.add(new HomecontrolJaxbProvider());
        return result;
    }
}
