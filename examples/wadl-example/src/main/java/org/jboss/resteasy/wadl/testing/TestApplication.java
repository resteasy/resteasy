package org.jboss.resteasy.wadl.testing;

import org.jboss.resteasy.wadl.testing.form.FormResource;
import org.jboss.resteasy.wadl.testing.locator.Parent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestApplication extends javax.ws.rs.core.Application {
    HashSet<Object> singletons = new HashSet<Object>();

    public TestApplication() {
//        singletons.add(new HelloMartianResource());
//        singletons.add(new Smoke());
        singletons.add(new FormResource());
        singletons.add(new Parent());
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
