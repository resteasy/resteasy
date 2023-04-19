package org.jboss.resteasy.test.cdi.ejb.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class EJBCDIValidationApplication extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(
                EJBCDIValidationStatelessResource.class,
                EJBCDIValidationStatefulResource.class,
                EJBCDIValidationSingletonResource.class));
    }
}
