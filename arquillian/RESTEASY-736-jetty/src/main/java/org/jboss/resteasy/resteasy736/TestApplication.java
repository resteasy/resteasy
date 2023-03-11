package org.jboss.resteasy.resteasy736;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 15, 2012
 */
@Provider
public class TestApplication extends Application {
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(TestResource.class);
        return classes;
    }
}
