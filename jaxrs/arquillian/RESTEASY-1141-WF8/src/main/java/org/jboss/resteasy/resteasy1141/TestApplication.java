package org.jboss.resteasy.resteasy1141;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ext.Provider;


/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Provider
public class TestApplication extends Application {

    public Set<Class<?>> getClasses()
    {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(TestResource.class);
        return classes;
    }
}
