package org.jboss.resteasy.jsapi.testing;

import org.jboss.resteasy.jsapi.testing.sub.BookImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestApplication extends javax.ws.rs.core.Application {
    HashSet<Object> singletons = new HashSet<Object>();

    public TestApplication() {
        singletons.add(new SmokeTestResource());
        singletons.add(new CachedResource());
        singletons.add(new AddImpl());
        singletons.add(new MinusImpl());
        singletons.add(new BookImpl());
    }

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
