package org.jboss.resteasy.resteasy1223;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;


/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Provider
public class TestApplication extends Application {

    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(YamlResource.class);
        return classes;
    }
}
