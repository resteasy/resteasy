package org.jboss.resteasy.resteasy1236;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class JApplication extends Application {
    public Set<Class<?>> getClasses()
    {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(YamlProviderResource.class);
        return classes;
    }
}
